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

function getTotalRisk( address, recursions ) {
	return db.Transaction.aggregate(
		{ $match: { "out.addr": address } }
		,{ $sort: { height: -1 } }
		,{$limit: 1}
		,{ $project:
		 {
		 	height: "$height",
		    out: {
		    	$filter: { input: "$out", as: "out", cond: { $eq: [ "$$out.addr", address ] } }
		    },
		    inputs: "$inputs"
		 }
		}
		,{ $unwind: "$out" }
		,{ $project: {
			  address: "$out.addr",
			  value: "$out.value",
			  input_addr:"$inputs.prev_out.addr"
			}
		}
		,{ $unwind: "$input_addr" }
		,{ $graphLookup:
			{
				from: "Transaction",
				startWith: "$input_addr",
				connectFromField: "inputs.prev_out.addr",
				connectToField: "out.addr",
				as: "source",
				maxDepth: recursions,
				depthField: "depth"
			}
		}
		,{ $unwind: "$source" }
		,{ $project:
			{
				_id: "$_id",
				coin: "$value",
				address: "$address",
				height: "$height",
				sourceTX: "$source._id",
				sourceAddr: "$source.inputs.prev_out.addr",
				depth: "$depth",
				blockHeight: "$source.block_height"
			}
		}
	    ,{ $unwind: "$sourceAddr" }
	    ,{ $sort: { "blockHeight": -1 } }
	);
}