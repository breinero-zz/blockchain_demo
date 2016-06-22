package com.mongodb.bitcoin.websocket;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/15/2016.
 */
public class MessageRouter {

    private final Map<String, Handler> handlers = new HashMap<String, Handler>();

    public void route( final String msg ) {
        Document doc = Document.parse( msg );
        Handler handler = handlers.get( doc.getString( "op" ) );

        if( handler == null )
            System.out.println( "Received unhandled message. "+msg );

        else {
            try {
                handler.Handle( doc.get( "x" ).toString() );
            } catch ( Exception e ) {
                System.out.println( e.getMessage() );
            }
        }
    }

    public void setHandler( Handler handler ) {
        handlers.put( handler.getName(), handler );
    }

}
