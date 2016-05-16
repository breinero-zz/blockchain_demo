package com.mongodb.bitcoin.websocket;

import org.bson.Document;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by brein on 5/2/2016.
 */
@WebSocket
public class ClientWebSocket {

    private Session session;
    private MessageRouter router = new MessageRouter();

    CountDownLatch latch= new CountDownLatch(1);


    public void addHandler ( Handler handler ) {
        router.setHandler( handler );

        String message = handler.getMessage();
        if( message != null )
            sendMessage( handler.getMessage() );
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {
        //System.out.println("Message received from server:" + message);
        router.route(  Document.parse( message ) );
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected to server");
        this.session=session;
        latch.countDown();
    }

    @OnWebSocketClose
    public void onClose( int statusCode, String reason ) {
        System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
        this.session = null;
        //this.latch.countDown(); // trigger latch
    }

    public void sendMessage(String str) {
        try {
            session.getRemote().sendString(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}