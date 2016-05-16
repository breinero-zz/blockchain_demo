package com.mongodb.bitcoin.websocket;

import org.bson.Document;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by brein on 5/15/2016.
 */
public class MessageRouter {

    private final Map<String, Handler> handlers = new HashMap<String, Handler>();

    public void route( final Document msg ) {
        String op = (String) msg.get( "op" );

        Handler handler = handlers.get( op );
        try {
            handler.Handle(msg);
        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
        }
    }

    public void setHandler( Handler handler ) {
        handlers.put( handler.getName(), handler );
    }

}
