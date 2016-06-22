package com.bryanreinero.bitcoin.aggregation;

import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by brein on 6/19/2016.
 */
public class TestUnspentAggregation {

    private final ObjectMapper mapper;
    private final MongoClient mongoClient;
    private final Morphia morphia;
    private final Datastore ds;

    public TestUnspentAggregation() {
        mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.map(BlockHeader.class).map(Transaction.class);
        morphia.setUseBulkWriteOperations( true );
        ds = morphia.createDatastore( mongoClient, "bitcoin");
    }

    @Test
    public void TestWallet() {
        String  path = Paths.get("").toAbsolutePath().toString();

        try {
            Transaction tx =
                    mapper.readValue(
                            new File(path +"/src/test/resources/TestTransaction.json" ), Transaction.class
                    );


            Map<String, Wallet> wallets = UnspentCoinAggregator.breakOutTxByAddr( tx );

            assert( wallets.size() == 2 );
            assert( wallets.containsKey( "17Mh4YwmDLQfqJfF7iCpqUc9WuTMAnRXEo" ) );
            assert( wallets.containsKey( "17jgdoJpzqXiyjULCoKxWSgb4ta95bkHMS" ) );
        } catch (IOException e) {
            e.printStackTrace();
            assert( false );
        }
    }
}
