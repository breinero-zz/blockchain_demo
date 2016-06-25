package com.mongodb.bitcoin.websocket;

import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by brein on 6/4/2016.
 */
public class BlockChainRetriever {

    private final String baseURI = "https://blockchain.info/rawblock/";
    private final static String latestBlockURL = "https://blockchain.info/latestblock";
    private CloseableHttpClient httpclient = HttpClients.createDefault();

    public String getLastBlockData() {
        String bString = null;
        try {
            CloseableHttpResponse response = httpclient.execute(
                    new HttpGet( latestBlockURL )
            );

            HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();
                try {
                    bString = CharStreams.toString(new InputStreamReader( instream, "UTF-8"));
                } finally {
                    instream.close();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return bString;
    }

    public String getBlockChainData(  String id ) {
        String bString = null;
        try {
            CloseableHttpResponse response = httpclient.execute(
                    new HttpGet( baseURI + id )
            );

            HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();
                try {
                    bString = CharStreams.toString(new InputStreamReader( instream, "UTF-8"));
                } finally {
                    instream.close();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return bString;
    }
}
