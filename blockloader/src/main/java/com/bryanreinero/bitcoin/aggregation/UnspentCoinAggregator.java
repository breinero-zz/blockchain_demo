package com.bryanreinero.bitcoin.aggregation;

import com.bryanreinero.bitcoin.Input;
import com.bryanreinero.bitcoin.Output;
import com.bryanreinero.bitcoin.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.spark.api.java.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.bson.Document;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * Created by brein on 6/19/2016.
 */
public class UnspentCoinAggregator implements Serializable {


    public static Map<String, Wallet> breakOutTxByAddr( Transaction tx ) {

        Map<String, Wallet> wallets = new HashMap();

        List<Input> inputs = tx.getInputs();

        for( Input input : inputs ) {
            String address = input.getPrev_out().getAddr();
            Wallet wallet = null;
            if( ( wallet = wallets.get( address ) ) == null )
                wallet = new Wallet( address );

            Record record = new Record();
            record.setTx( tx.getHash() );
            record.setType( Record.Type.input );
            record.setValue( input.getPrev_out().getValue() );
            record.setTx_Index( input.getPrev_out().getTx_index() );

            wallet.getRecords().add( record );
            wallets.put( wallet.getAddress(), wallet );
        }

        List<Output> outputs = tx.getOut();
        for( Output output : outputs ) {
            String address = output.getAddr();

            Wallet wallet = null;
            if( ( wallet = wallets.get( address ) ) == null )
                wallet = new Wallet( address );

                Record record = new Record();
                record.setTx( tx.getHash() );
                record.setType( Record.Type.output );
                record.setValue( output.getValue() );
                record.setTx_Index( output.getTx_index() );

            wallet.getRecords().add( record );
            wallets.put( wallet.getAddress(), wallet );
        }

        return wallets;
    }

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("UnspentAggregator")
                .set("spark.app.id", "UnspentAggregator")
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/bitcoin.Transaction")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/bitcoin.account" );


        JavaSparkContext jsc = new JavaSparkContext(conf);
        JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);

        JavaPairRDD<String, Wallet> wallets = rdd.flatMapToPair(
                new PairFlatMapFunction<Document, String, Wallet> () {
                    ObjectMapper om = new ObjectMapper();

                    @Override
                    public Iterable<Tuple2<String, Wallet>> call(Document document) throws Exception {

                        Map<String, Wallet> wallets = breakOutTxByAddr( Converter.mapTx( document ) );
                        List< Tuple2<String, Wallet> > tuples = new ArrayList<>();

                        wallets.forEach((s, wallet) -> {
                            tuples.add( new Tuple2<>( wallet.getAddress(), wallet ) );
                        }
                        );
                        return  tuples;
                    }
                }
        );

        JavaPairRDD<String, Wallet> reducedWallets = wallets.reduceByKey(
                new ReduceWalletFunction()
        );

        JavaRDD<Document> accounts = reducedWallets.map(
                new Function<Tuple2<String, Wallet>, Document>() {
                    @Override
                    public Document call(Tuple2<String, Wallet> v1) throws Exception {

                        Document balance =  new Document( "_id", v1._1() );

                        final Long[] total = {0L};
                        List<Document> outputs = new ArrayList();
                        v1._2().getRecords().forEach(
                                record -> {
                                    if( record.getType().equals( Record.Type.output ) ) {
                                        Document output = new Document( "txId", record.getTx() );
                                        output.put( "tx_index", record.getTx_Index() );
                                        output.put( "satoshi", record.getValue() );
                                        total[0] += record.getValue();
                                        outputs.add( output );
                                    }
                                }
                        );

                        balance.put( "outputs", outputs );
                        balance.put( "total", total[0] );
                        return balance;
                    }
                }
        );

        MongoSpark.save( accounts );
    }

}
