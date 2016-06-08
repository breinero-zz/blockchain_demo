package com.mongodb.bitcoin.websocket;

import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.mongodb.MongoClient;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.URI;

/**
 * Created by brein on 5/2/2016.
 */
public class BitcoinWebSocketClient implements Consumer<BlockHeader> {

    private final ClientWebSocket socket;
    private final MongoClient mongoClient ;
    private final Morphia morphia;
    private final Datastore ds;


    public BitcoinWebSocketClient () {
        socket = new ClientWebSocket();
        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.map(BlockHeader.class).map(Transaction.class);
        ds = morphia.createDatastore( mongoClient, "bitcoin");
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

            //socket.addHandler( new StatusHandler() );
            socket.addHandler( new BlockHandler( this ) );
            //socket.addHandler( new UnconfirmedTransactionHandler( mongoClient) );
            socket.sendMessage( "{\"op\":\"ping_block\"}" );
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

        BitcoinWebSocketClient client = new BitcoinWebSocketClient();
        client.run( dest );

    }

    @Override
    public void consume( BlockHeader block ) {
        ds.save( block );
    }
}
