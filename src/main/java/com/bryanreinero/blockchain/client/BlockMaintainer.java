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
                        System.out.println( "received block" );
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

        log.info( "Received new block "+header.getHash() );
        log.info( "Getting latest block" );
        // delete pending transactions
        //header.getTxIndexes().forEach(
        //        index -> {
        //            ds.update(
        //                    ds.createQuery(Transaction.class).filter( "tx_index", index ),
        //
        //                    );
        //        }
        //);
        ds.save( header );

        // get full block
        try{
            getAllBlocks( header.getHash() );
        } catch( Exception e ) {
            log.warning( "Couldn't retrieve block "+header.getHash()+" "+e.getMessage() );
        }

    }

    public void handleTransaction ( Transaction transaction ) {
        ds.save( transaction );
    }

    public void getFullBlock( String hash ) throws Exception {
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
    }

    public void getAllBlocks( String hash ) throws Exception {
        do {
            log.info( "Getting full block "+hash );
            String blockData = retriever.getBlockChainData(hash);
            Block b = mapper.readValue(blockData, Block.class);

            b.getTx().forEach(
                    t -> {
                        t.setBlockHash( b.getHash() );
                        t.setBlock_height( b.getHeight() );
                        //t.getOut().forEach(
                        //        o -> {
                        //            o.setTxID( t.getHash() );
                        //            o.setBlockHeight( b.getHeight() );
                        //            o.setTx_index( t.getTx_index() );
                        //            ds.save( o );
                        //        }
                        //);
//
                        //t.getInputs().forEach(
                        //        i -> {
                        //            String addr = i.getPrev_out().getAddr();
                        //            Integer tx_index = i.getPrev_out().getTx_index();
                        //        }
                        //);
                        ds.save( t );
                    }
            );
            hash = b.getPrev_block();
            ds.save( b.getBlockHeader() );
        } while( hash != null && !hash.isEmpty() );
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
