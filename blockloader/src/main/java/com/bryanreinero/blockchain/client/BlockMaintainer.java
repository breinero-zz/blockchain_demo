package com.bryanreinero.blockchain.client;

import com.bryanreinero.bitcoin.Block;
import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.bitcoin.websocket.BlockChainRetriever;
import com.mongodb.bitcoin.websocket.Handler;
import com.mongodb.bitcoin.websocket.WebSocketApplication;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by brein on 6/8/2016.
 */
public class BlockMaintainer {

    static Logger log = Logger.getLogger( BlockMaintainer.class.getName() );

    private final WebSocketApplication client;
    private final ObjectMapper mapper;
    private final MongoClient mongoClient;
    private final Morphia morphia;
    private final Datastore ds;
    private final BlockChainRetriever retriever;
    private Replicator replicator;

    private boolean firstBlock = true;

    private class Replicator extends Thread {

        private String hash;
        public Replicator ( String hash ) {
            this.hash = hash;
        }

        @Override
        public void run() {
            try {
                do {
                    Block b = getFullBlock( hash );
                    hash = b.getPrev_block();
                } while( hash != null && !hash.isEmpty() );
            } catch (InterruptedException e) {
                log.info( "interrupted at block "+hash );
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.info( "failed retrieving block "+hash);
                log.severe( e.getMessage() );
            }
        }
    }

    public BlockMaintainer (){

        client = new WebSocketApplication( "wss://ws.blockchain.info/inv" );
        mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.map(BlockHeader.class).map(Transaction.class);
        morphia.setUseBulkWriteOperations( true );
        ds = morphia.createDatastore( mongoClient, "bitcoin");
        retriever = new BlockChainRetriever();
    }

    public void start() throws Exception  {

        log.info( "starting BlockMaintainer" );
        client.run();

        client.addHandler(
                new Handler() {
                    @Override
                    public void Handle(String msg) throws Exception {
                        handleBlock( mapper.readValue(msg, BlockHeader.class) );
                    }

                    @Override
                    public String getName() { return "block"; }

                    @Override
                    public String getMessage() { return "{\"op\":\"blocks_sub\"}"; }
                }
        );

        client.addHandler(
                new Handler() {
                    @Override
                    public void Handle(String msg) throws Exception {
                        //log.info( "received transaction" );
                        handleTransaction( mapper.readValue(msg, Transaction.class) );
                    }

                    @Override
                    public String getName() { return "utx"; }

                    @Override
                    public String getMessage() { return  "{\"op\":\"unconfirmed_sub\"}"; }
                }
        );

        log.info( "Getting latest block" );
        client.sendMessage( "{\"op\":\"ping_block\"}" );
    }

    public void handleBlock ( BlockHeader header ) throws IOException {
        if( firstBlock ) {
            log.info( "Received first block "+header.getHash() );
            firstBlock = false;
            replicator = new Replicator( header.getHash() );
            replicator.start();
            return;
        }

        log.info( "Received block "+header.getHash() );
        // first save the new header
        ds.save( header );

        // update each transaction
        UpdateOperations<Transaction> ops
                = ds.createUpdateOperations( Transaction.class ).add( "blockHash", header.getHash() );
        ops.add( "block_height", header.getHeight() );
        header.getTxIndexes().forEach(
                i -> {
                    ds.update(
                            ds.createQuery(Transaction.class).field("tx_index").equal(i),
                            ops,
                            true
                    );
                }
        );
    }

    public void handleTransaction ( Transaction transaction ) {
        ds.save( transaction );
    }

    public Block getFullBlock( String hash ) throws Exception {
        log.info( "Getting full block "+hash );
        String blockData = retriever.getBlockChainData( hash );
        Block b = mapper.readValue( blockData, Block.class );
        ds.save( b.getBlockHeader() );
        b.getTx().forEach(
                t -> {
                    t.setBlockHash( hash );
                    ds.save( t );
                }
        );
        return b;
    }

    public static void main ( String[] args ) {
        BlockMaintainer maintainer = new BlockMaintainer();
        try {
            maintainer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
