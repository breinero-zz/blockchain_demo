package com.bryanreinero.blockchain.client;

import com.bryanreinero.bitcoin.Block;

/**
 * Created by brein on 6/6/2016.
 */
public class BlockClient {

    public static void main( String[] args ) {
        AysncBlockChainClient client = new AysncBlockChainClient();

        String hash = "0000000000000000014e23da37f3bde8e1a3510bf08915675a1d235c83777c99";
        do {
            try {
                Block block = client.getBlockByHash( hash );
                System.out.println( "Block: "+block.getHash()+", height:  "+block.getHeight() );
                hash = block.getPrev_block();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while ( hash != null );
    }

}