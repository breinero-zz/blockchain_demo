package com.mongodb.bitcoin.websocket;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 6/4/2016.
 */
public class BlockRetriever {

    private final Set<String> blockhashses = new HashSet<>();
    private final MongoCollection<Document> coll;
    private final BlockChainRetriever retriever;

    public BlockRetriever(MongoCollection coll, BlockChainRetriever retriever) {
        this.coll = coll;
        this.retriever = retriever;
    }

    public Document getBlock( String hash ) {

        Document block =
                coll.find( new Document(  "_id", hash) ).limit( 1 ).first();

        if ( block != null )
            return block;

        block = Document.parse( retriever.getBlockChainData( hash ) );

        coll.insertOne( block );

        return block;
    }
}
