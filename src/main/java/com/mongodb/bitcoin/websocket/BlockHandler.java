package com.mongodb.bitcoin.websocket;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


/**
 * Created by brein on 5/15/2016.
 */
public class BlockHandler implements Handler {

    private final String op = "block";
    private final String message = "{\"op\":\"blocks_sub\"}"; //"{\"op\":\"ping_block\"}";
    private final MongoClient client;
    private static final String collectionName = "blocks";
    private static final String databaseName = "bitcoin";

    private final MongoCollection collection;

    public BlockHandler( MongoClient client) {
        this.client = client;
        collection = client.getDatabase( databaseName ).getCollection( collectionName );
    }

    @Override
    public void Handle(Document msg) {

        collection.insertOne( msg);
    }

    @Override
    public String getName() {
        return op;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
