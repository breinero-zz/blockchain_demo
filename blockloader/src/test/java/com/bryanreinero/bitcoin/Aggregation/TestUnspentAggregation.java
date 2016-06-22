package com.bryanreinero.bitcoin.Aggregation;

import com.bryanreinero.bitcoin.BlockHeader;
import com.bryanreinero.bitcoin.Transaction;
import com.bryanreinero.bitcoin.aggregation.UnspentCoinAggregator;
import com.bryanreinero.bitcoin.aggregation.Wallet;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

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
        Transaction tx = ds.find( Transaction.class ).get();
               // .field( "_id" ).equals( "08f906a56b8d42312171b966a8c38a6e12aebbf1910ce1068746a0d962ae502b" );

        Map<String, Wallet> wallets = UnspentCoinAggregator.breakOutTxByAddr( tx );

        assert( wallets.size() == 3 );
        assert( wallets.containsKey( "12GV8BDpTFBsZrteSRkJ3gBWGxmPpLHHB7" ) );
        assert( wallets.containsKey( "1H961QbXzizYjvafNwu5YigjQ2ZRD26sDd" ) );
        assert( wallets.containsKey( "1EVmQtvNooZSC9KvFGqqGCP6ADbNDJGa5P" ) );
    }
}
