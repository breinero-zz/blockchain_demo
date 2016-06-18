package com.mongodb.bitcoin.websocket;

import com.bryanreinero.bitcoin.ApplicationContext;
import com.bryanreinero.bitcoin.Block;
import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.google.common.io.CharStreams;
import com.mongodb.MongoClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by brein on 6/4/2016.
 */
public class BlockChainRetrieverTest {

    private final String baseURI = "https://blockchain.info/";
    public static final String blockPath = "rawblock/";

    private String hash = "0000000000000bae09a7a393a8acded75aa67e46cb81f7acaa5ad94f9eacd103";

    private BlockHandler blockHandler;
    CloseableHttpClient httpclient = HttpClients.createDefault();

    private final MongoClient mongoClient ;
    private final Morphia morphia;
    private final Datastore ds;

    public BlockChainRetrieverTest () {
        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.map(BlockHeader.class).map(Transaction.class);
        ds = morphia.createDatastore( mongoClient, "bitcoin");
    }

    @Test
    public void getBlockChainData() {
        String blockS = null;
        try {
            CloseableHttpResponse response = httpclient.execute(
                    new HttpGet( baseURI + blockPath + hash )
            );

            HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();
                try {
                    blockS = CharStreams.toString(new InputStreamReader( instream, "UTF-8"));
                } finally {
                    instream.close();
                }
            }

            Block block = ApplicationContext.INSTANCE.getMapper().readValue( blockS, Block.class );
            assert( block.getHash().equals( hash ) );

            BlockHeader header = block.getBlockHeader();
            ds.save( header );

            block.getTx().forEach(
                    t -> { ds.save( t ); }
            );


        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
