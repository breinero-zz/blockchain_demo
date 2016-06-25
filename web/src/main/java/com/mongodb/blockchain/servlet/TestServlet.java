package com.mongodb.blockchain.servlet;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.nio.file.Paths;

/**
 * Created by brein on 6/23/2016.
 */
public class TestServlet {

    MongoClient client = new MongoClient();
    MongoDatabase db = client.getDatabase("bitcoin");
    String filename = Paths.get("").toAbsolutePath().toString() +"/web/src/main/webapp/WEB-INF/aggregations/transactionLineage.json";

    public void TestReformat( String txId ) {
        try {

            LineageBuilder builder = new LineageBuilder( filename, db );
            System.out.println ( builder.getAncestry(  txId ) );

        }catch( Exception e ) {
            e.printStackTrace();
            assert( false );
        }
    }

    public static void main( String[] args ) {
        TestServlet serv = new TestServlet();
        serv.TestReformat( "65feee3ec73e0eede90d8b5ccb8937f1692144678fb540074299d904b5737f1a" );
    }
}
