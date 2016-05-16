package com.mongodb.bitcoin.websocket;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Created by brein on 5/15/2016.
 */
public class UnconfirmedTransactionHandler implements Handler {

    private final String name = "utx";
    private final String message = "{\"op\":\"unconfirmed_sub\"}";
    private final MongoClient client;
    private static final String collectionName = "unconfirmed";
    private static final String databaseName = "bitcoin";

    private final MongoCollection collection;


    public UnconfirmedTransactionHandler(MongoClient client) {
        this.client = client;
        collection = client.getDatabase( databaseName ).getCollection( collectionName );
    }

    @Override
    public void Handle(Document msg) {
        //System.out.println( msg );
        collection.insertOne( msg );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
