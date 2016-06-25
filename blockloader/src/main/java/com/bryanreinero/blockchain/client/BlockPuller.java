package com.bryanreinero.blockchain.client;

import com.bryanreinero.bitcoin.Block;
import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.bryanreinero.firehose.util.Result;
import com.bryanreinero.firehose.util.WorkerPool;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.bitcoin.websocket.BlockChainRetriever;
import com.mongodb.client.MongoCollection;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by brein on 6/23/2016.
 */
public class BlockPuller {

    private static Logger log = Logger.getLogger( BlockPuller.class.getName() );

    private final ExecutorService es;
    private final CompletionService<String> cs;

    private final ObjectMapper mapper;
    private final MongoClient mongoClient;
    private final Datastore ds;
    private final Morphia morphia;
    private final BlockChainRetriever retriever;


    private WorkerPool<Result<String>> pool;

    public BlockPuller( ) {

        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.map(BlockHeader.class).map(Transaction.class);
        morphia.setUseBulkWriteOperations( true );

        mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);


        ds = morphia.createDatastore( mongoClient, "bitcoin");
        retriever = new BlockChainRetriever();

        es = Executors.newFixedThreadPool( 1 );
        cs = new ExecutorCompletionService<String>( es );
    }

    public List<Integer> getGaps() {
        List<Integer> missing = new ArrayList<>();
        final Integer[] current = {0};

        Document sort = new Document( "height", 1 );
        MongoCollection<Document> headers = mongoClient.getDatabase("bitcoin").getCollection("BlockHeader");
        headers.find( ).sort( sort ).forEach(
                (com.mongodb.Block<? super Document>) document -> {
                    Integer height = document.getInteger( "height" );

                   while(  current[0] < height ) {
                       missing.add(current[0]);
                       current[0] = current[0] + 1;
                   }
                }
        );
        return missing;
    }


    public void run() throws Exception {

        BlockHeader earliest = ds.find( BlockHeader.class).order("height").limit(1).get();

        String hash = earliest.getPrev_block();

        do {
            hash = parseAndPersist(
                    retriever.getBlockChainData(hash) ).getPrev_block();
        } while( hash != null || !hash.isEmpty() );
    }


    public Block parseAndPersist( String s ) throws IOException {
        Block b = mapper.readValue( s, Block.class );
        ds.save( b.getBlockHeader() );
        b.getTx().forEach(
                t -> {
                    t.setBlockHash( b.getHash() );
                    ds.save( t );
                }
        );
        return b;
    }

    public static void main ( String[] args ) {
        
        try {
            BlockPuller puller = new BlockPuller);
            puller.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
