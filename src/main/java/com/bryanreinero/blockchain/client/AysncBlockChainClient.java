package com.bryanreinero.blockchain.client;

import com.bryanreinero.bitcoin.ApplicationContext;
import com.bryanreinero.bitcoin.Block;
import com.bryanreinero.bitcoin.Transaction;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * Created by brein on 6/6/2016.
 */
public class AysncBlockChainClient {

    private final CloseableHttpClient httpclient;
    private final String url = "https://blockchain.info/";
    private static final String blockPath = "rawblock/";
    private static final String transactionPath = "rawtx/";

    public AysncBlockChainClient() {
        httpclient = HttpClients.createDefault();
        ObjectMapper mapper = ApplicationContext.INSTANCE.getMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String get( String path ) throws ExecutionException, InterruptedException, IOException {
        HttpGet request = new HttpGet( url + path );
        HttpResponse response =  httpclient.execute( request );
        HttpEntity entity = response.getEntity();

        String data = null;
        if (entity != null) {

            InputStream instream = null;
            instream = entity.getContent();
            data = CharStreams.toString(new InputStreamReader( instream, "UTF-8"));
            instream.close();
        }

        return data;
    }

    public Block getBlockByHash( String hash ) throws Exception {
        try {
            return ApplicationContext.INSTANCE.getMapper().readValue(
                    get( blockPath+hash ),
                    Block.class
            );
        } catch ( ExecutionException | InterruptedException e ) {
            throw new Exception( "Failed to retrieve block. "+hash, e );
        }
    }

    public Transaction getTransactionByHash(String hash ) throws Exception {
        try {
            return ApplicationContext.INSTANCE.getMapper().readValue(
                    get( transactionPath+hash ),
                    Transaction.class
            );
        } catch ( ExecutionException | InterruptedException e ) {
            throw new Exception( "Failed to retrieve transaction. Hash: "+hash, e );
        }
    }

    public Transaction getTransactionByIndex( Long index ) throws Exception {
        try {
            return ApplicationContext.INSTANCE.getMapper().readValue(
                    get( transactionPath+index ),
                    Transaction.class
            );
        } catch ( ExecutionException | InterruptedException e ) {
            throw new Exception( "Failed to retrieve transaction. index: "+index, e );
        }
    }
}

