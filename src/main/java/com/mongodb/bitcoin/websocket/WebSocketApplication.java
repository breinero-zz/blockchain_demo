package com.mongodb.bitcoin.websocket;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by brein on 5/2/2016.
 */
@WebSocket
public class WebSocketApplication {

    static Logger log = Logger.getLogger( WebSocketApplication.class.getName() );
    private static final int MAX_MESSAGE_SIZE = 1000000;

    private Session session;

    private  final SslContextFactory sslContextFactory;
    private  final Resource keyStoreResource;
    private  final org.eclipse.jetty.websocket.client.WebSocketClient client;
    private final String  uri;

    private final CountDownLatch latch= new CountDownLatch(1);

    private final Map<String, Handler> handlers = new HashMap<String, Handler>();

    public WebSocketApplication( String uri ) {

        keyStoreResource = Resource.newResource(this.getClass().getResource("/truststore.jks"));
        sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");
        client = new org.eclipse.jetty.websocket.client.WebSocketClient(sslContextFactory);
        client.setMaxTextMessageBufferSize( MAX_MESSAGE_SIZE );
        this.uri = uri;
    }

    public void run() throws Exception {
        client.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();

        client.connect( this, new URI( uri ), request);
        latch.await();
    }

    public void sendMessage ( String message ) throws Exception {
        try {
            session.getRemote().sendString( message );
        } catch (IOException e) {
            throw new Exception( "Could not send message "+message+" to "+uri );
        }
    }

    public void addHandler( Handler handler ) throws Exception {

        handlers.put( handler.getName(), handler );

        try {
            latch.await();
            session.getRemote().sendString( handler.getMessage() );
        } catch ( IOException | InterruptedException e) {
            throw new Exception( "Failed to send message: "+handler.getMessage(), e );
        }
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {

        DBObject doc = (DBObject) JSON.parse( message );
        Handler handler = handlers.get( doc.get( "op" ) );

        if( handler == null )

            log.warning( "Received unhandled message. "+ message );

        else {
            try {
                handler.Handle( doc.get( "x" ).toString() );
            } catch ( Exception e ) {
               log.severe( e.getMessage() );
            }
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.info("Connected to server");
        this.session=session;
        latch.countDown();
    }

    @OnWebSocketClose
    public void onClose( int statusCode, String reason ) {
        log.info("Connection closed. code: "+statusCode+", reason: "+reason);
        this.session = null;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
