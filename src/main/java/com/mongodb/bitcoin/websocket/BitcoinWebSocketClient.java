package com.mongodb.bitcoin.websocket;

import com.mongodb.MongoClient;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

/**
 * Created by brein on 5/2/2016.
 */
public class BitcoinWebSocketClient {

    private final ClientWebSocket socket;
    private final MongoClient mongoClient ;

    public BitcoinWebSocketClient () {
        socket = new ClientWebSocket();
        mongoClient = new MongoClient();
    }

    public void run( String dest ) {
        SslContextFactory sslContextFactory = new SslContextFactory();
        Resource keyStoreResource = Resource.newResource(this.getClass().getResource("/truststore.jks"));
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");
        WebSocketClient client = new WebSocketClient(sslContextFactory);

        try {

            client.start();
            URI uri = new URI(dest);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);
            socket.getLatch().await();

            socket.addHandler( new StatusHandler() );
            socket.addHandler( new BlockHandler( mongoClient ) );
            socket.addHandler( new UnconfirmedTransactionHandler( mongoClient) );
            Thread.sleep(10000l);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String dest = "wss://ws.blockchain.info/inv";
        //dest = "wss://echo.websocket.org";

        BitcoinWebSocketClient client = new BitcoinWebSocketClient();
        client.run( dest );

    }
}
