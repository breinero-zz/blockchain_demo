package com.mongodb.blockchain.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/23/2016.
 */
public class TestServlet {

    ObjectMapper om = new ObjectMapper();

    public void TestReformat() {
        String  path = Paths.get("").toAbsolutePath().toString();

        String inputFilename = path +"/web/src/main/webapp/transactionLineage2.json";
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(inputFilename));
            String json = "";
            String line = null;
            while (( line = reader.readLine()) != null) {
                json += line;
            }

            Document lineage = Document.parse( json );
            LineageServlet servlet = new LineageServlet();

            Node parent = new Node();
            parent.setName( "65feee3ec73e0eede90d8b5ccb8937f1692144678fb540074299d904b5737f1a" );
            parent.setAddress( "3F1KFNMgmx2GnRYVGVcvf1gQn5bW2fCaJz" );
            List<String> childNames = new ArrayList<>();
            childNames.add( "38x1bqLUy9iL8pPx4pX9mKujzhRo4Ax9hH");
            childNames.add( "1Q9vyqFPMEGN2CB1CGhYn5YFRGicy27WND");
            parent.setChildNames( childNames );

            servlet.reformat(  (List<Document>)lineage.get( "result" ), parent, 0 );
            System.out.println( om.writeValueAsString( parent ) );

        }catch( Exception e ) {
            e.printStackTrace();
            assert( false );
        }
    }

    public static void main( String[] args ) {
        TestServlet serv = new TestServlet();

        serv.TestReformat();
    }
}
