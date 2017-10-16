function getEncumberanceRisk( address, chainHeight ) {
	var encumberances = [];
	db.Transaction.aggregate(
		{ $match: { "out.addr": address } }
		,{ $limit: 1000 }
		,{ $project:
			{
				depth: {
					$let: {
						vars: { depth: { $subtract: [ "$block_height", chainHeight ] } },
						in: {
							$cond:{
								if: { $gte: [ "$$depth", 6 ]  },  then: 6, else: "$$depth" }
							}
						}
				},
				out: {
					$filter: { input: "$out", as: "out", cond: { $eq: [ "$$out.addr", address ] } }
				}
			}
		}
		,{ $unwind: "$out" }
		,{
			$project: {
				_id: 0,
				address: "$out.addr",
				tx_index: "$out.tx_index",
				coin: "$out.value",
				depth: "$depth"
			}
		}
	).forEach( function ( output ) {
		var cur = db.Transaction.find( { "inputs.prev_out.addr": output.address, "inputs.prev_out.tx_index": output.tx_index }  );
		if( ! cur.hasNext() )
			encumberances.push( output );
	});
	return encumberances;
}


function getEncumberanceRisks( chainHeight ) {
	return db.Output.aggregate(
		{ $project:
			{
				_id: 0,
				address: "$addr",
				coin: "$value",
				depth: {
					$let: {
						vars: { depth: { $subtract: [ chainHeight, "$blockHeight" ] } },
						in: {
							$cond:{
								if: { $gte: [ "$$depth", 6 ]  },  then: 6, else: "$$depth" }
							}
						}
				}
			}
		},
		{
			$group: {
				_id: {
					address: "$address",
					depth: "$depth"
				},

				coin: { $sum: "$coin" }
			}
		},
		{ $sort: { "_id.depth": 1, "_id.address": 1 } }
	)
}

function getCurrentBlockHeight() {
	return db.BlockHeader.find( {}, { height: 1, _id: 0 } ).sort( { height: -1} ).limit( 1 ).next().height;
}

function getTotalRisk( address, chainHeight ) {
	return db.runCommand(
	{
		"aggregate": "Transaction",
		"allowDiskUse" : true,
		"pipeline": [
		{ "$match": { "out.addr": address } }
		,{ "$sort": { "height": -1 } }
		,{"$limit": 1}
		,{ "$project":
			{
				"height": "$height",
			   "out": {
			   	"$filter": {
			   		"input": "$out",
			   		"as": "out",
			   		"cond": { "$eq": [ "$$out.addr", address ] } }
			   },
			   "inputs": "$inputs"
			}
		}
		,{ "$unwind": "$out" }
		,{ "$project": {
			  "to_address": "$out.addr",
			  "value": "$out.value",
			  "input_addr":"$inputs.prev_out.addr"
			}
		}
		,{ "$unwind": "$input_addr" }
		,{ "$graphLookup":
			{
				"from": "Transaction",
				"startWith": "$input_addr",
				"connectFromField": "inputs.prev_out.addr",
				"connectToField": "out.addr",
				"as": "source",
				"maxDepth": 10,
				"depthField": "depth"
			}
		}
		,{ "$unwind": "$source" }
		,{ "$project":
			{
				"_id": "$_id",
				"coin": "$value",
				"address": "$address",
				"height": "$height",
				"sourceTX": "$source._id",
				"sourceAddr": "$source.inputs.prev_out.addr",
				"depth": "$source.depth",
				"blockHeight": "$source.block_height"
			}
		}
	    ,{ "$unwind": "$sourceAddr" }
	    ,{ "$sort": { "blockHeight": 1 } }
	    ]
	}
	);
}

function getLineage( txId, depth ) {
	return db.runCommand(
	{
		"aggregate": "Transaction",
		"allowDiskUse" : true,
		"pipeline": [
		{ "$match": { "_id": txId } }
		,{ "$project": {
			  "input_addr":"$inputs.prev_out.addr"
			}
		}
		,{ "$unwind": "$input_addr" }
		,{ "$graphLookup":
			{
				"from": "Transaction",
				"startWith": "$input_addr",
				"connectFromField": "inputs.prev_out.addr",
				"connectToField": "out.addr",
				"as": "source",
				"maxDepth": depth,
				"depthField": "depth"
			}
		}
		,{ "$unwind": "$source" }
		,{ "$project":
			{
				"_id": 0,
				"toAddr":  "$source.out.addr",
				"fromtx": "$source._id",
				"address": "$source.inputs.prev_out.addr",
				"satoshi": "$source.inputs.prev_out.value",
				"depth": "$source.depth"
			}
		}
	    ,{ "$unwind": "$satoshi" }
	    ,{ "$unwind": "$address" }
	    ,{
	    	"$group": {
	    		"_id": "$depth",
	    		"txs": {
	    			$push: {
	    				"children": "$toAddr",
						"name": "$fromtx",
						"address": "$address",
						"satoshi": "$satoshi",
	    			}
	    		}
	    	}
	    }
	    ,{ "$sort": { "_id": 1 } }
	    //, { "$project": { _id: 0, "txs": "$txs" } }
	    ]
	}
	);
}


function getLineages() {
	var lineages = [];
	db.Transaction.find( { "blockHash": { "$exists": false } }, { "_id": 1, 'out': 1 } ).forEach(
		function( doc ) {
			//print( doc._id );
			for ( var i = 0; i < doc.out.length; i++ ) {
				lineages.push (
					getTotalRisk( doc.out[i].addr ).result
				);
			}
		}
	);
	return lineages;
}

function getCoinInflight() {
	return db.Transaction.aggregate(
		{ $match: { blockHash: { $exists: false } } }
		,{ $unwind: "$out" }
		,{ $unwind: "$inputs" }
		,{ $project:
			{
				to: "$out.addr",
				satoshi: "$out.value",
				inputs: "$inputs"
			}
		}
		,{ $group:
			{
				_id: "$to",
				total: { $sum: "$satoshi" },
				inputs: {
					$push: {
						from: "$inputs.prev_out.addr",
						amount: "$inputs.prev_out.value",
						transaction: "$inputs.prev_out.tx_index"
					}
				}
			}
		}
		,{ $project: {
				"account": "$_id",
				total: "$total",
				_id: 0,
				inputs: "$inputs"
			}
		}
	);
}

function recursiveAscentLineage( generations ) {

	var parent;
	var firstAncestors = [];
	for( var depth = generations.length -1; depth >= 0; depth-- ) {
		print( "depth: "+depth );

		// may be multiple children in each generation
		for( var i = 0; i < generations[depth].txs.length; i++ ) {

			var child = generations[ depth ].txs[i];
			print( "Generation: "+depth
				+", name: "+child.name
				+", satoshi: "+child.satoshi
				+", to: "+child.children	 );

			// copy child into the right format
			var node = {};
			node.name = child.name;
			node.address = child.address;
			node.satoshi = child.satoshi;
			node.toAddr = child.children;
			node.children = [];

			if( parent != undefined ) {

				print( "Checking for paternity on parent "+parent.name );			   // check if this (child) matches in parent
			   for( var j = 0; j < parent.toAddr.length; j++ ) {
			   	if( parent.toAddr[j] == node.address ) {
			   		print( "Match: { parent: "+parent.toAddr[j]+", child:"+node.address+" } ");
			   		parent.children.push( node );
			   		print( "child count now "+parent.children.length );
			   	} else {
			   		print( "Miss: { parent: "+parent.toAddr[j]+", child:"+node.address+" } ");
			   	}
			   }
			}
			parent = node;
		}

		if( ( generations.length -1)  == depth )
			firstAncestors.push( parent );
	}
	return firstAncestors;
}


function getGaps() {
	var missing = [];
	var current =0;
	db.BlockHeader.find( {}, { _id: 0, height: 1 }  ).sort( { height: 1 } )
	.forEach( function( doc ) {
		var gap = doc.height - current;
		if( gap > 0 ) {
			for ( var i = current; i < doc.height; i++ ) {
				missing.push( i )
			};

		}
		current = doc.height + 1;
	});
	return missing;
}