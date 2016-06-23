package com.bryanreinero.bitcoin.aggregation;

import com.bryanreinero.bitcoin.Transaction;
import org.bson.Document;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by brein on 6/22/2016.
 */
public class TestConversion {

    @Test
    public void TestConverter () {
        String  path = Paths.get("").toAbsolutePath().toString();

        String inputFilename = path +"/src/test/resources/TestDocumentTransaction.json";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFilename));

            String json = "";
            String line = null;
            while (( line = reader.readLine()) != null) {
                json += line;
            };

            Document document = Document.parse( json );
            Transaction transaction = Converter.mapTx( document );

            assert(
                    transaction.getHash().equals( "0000f010319caa7970a66a96fdafe82263209882d5a536c30cb8caca9377e4f0")
            );
            assert( transaction.getInputs().size() == 1 );
            assert( transaction.getOut().size() == 2 );

        } catch ( IOException e )  {
            e.printStackTrace();
            assert( false );
        }

    }
}
