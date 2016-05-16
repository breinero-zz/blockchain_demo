package com.mongodb.bitcoin.websocket;

import org.bson.Document;

/**
 * Created by brein on 5/15/2016.
 */
public class BlockSubscriber implements Handler {
    @Override
    public void Handle(Document msg) {
        System.out.println( msg );
    }

    @Override
    public String getName() {
        return "{\"op\":\"blocks_sub\"}";
    }

    @Override
    public String getMessage() {
        return null;
    }
}
