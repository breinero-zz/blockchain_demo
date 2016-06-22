package com.bryanreinero.bitcoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;

/**
 * Created by brein on 6/5/2016.
 */
public class TestBlockData {

    ClassLoader loader = this.getClass().getClassLoader();
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testInputParsing() {

        String  path = Paths.get("").toAbsolutePath().toString();
        try {
            Input input =
                    mapper.readValue(
                            new File( path+"/src/test/resources/TestInput.json" ), Input.class  );


            assert( input.getPrev_out().getSpent().equals( true ) );
            assert( input.getPrev_out().getAddr().equals( "1MHAHHpueH3hhBzKfpDuNxR7oDWPEg7jVL" ) );

        } catch (IOException e) {
            e.printStackTrace();
            assert( false );
        }
    }

    @Test
    public void testOutputParsing() {
        String  path = Paths.get("").toAbsolutePath().toString();
        try {

            Output output =
                mapper.readValue(
                            new File( path+"/src/test/resources/TestOutput.json" ), Output.class
                    );


            assert( output.getSpent().equals( true ) );
            assert( output.getAddr().equals( "1CHmRhDKM3uaz8R5N8SfJkTfQsbLhV3FSY" ) );

        } catch (IOException e) {
            e.printStackTrace();
            assert( false );
        }
    }

    @Test
    public void TestHeaderParsing() {
        String  path = Paths.get("").toAbsolutePath().toString();
        try {
            BlockHeader header =
                    mapper.readValue(
                            new File( path+"/src/test/resources/TestBlock.json" ), BlockHeader.class
                    );


            assert( header.getHash().equals( "0000000000000000014e23da37f3bde8e1a3510bf08915675a1d235c83777c99" ) );
            assert( header.getVer().equals( 4 ) );
            assert(
                    header.getPrev_block().equals( "00000000000000000185972cbd605b0be3c7c7830b1dc019a4fae67aa04faf0f" )
            );
            assert( header.getTime().equals( 1465274513L ) );
            assert(
                    header.getMrkl_root().equals( "1997747bbc9eb22256680d613856e5755b5737c3a319d7ecca7bc96263347d40")
            );
            assert( header.getFee().equals( 14617375L ) );
            assert( header.getBits().equals( 403014710 ) );
            assert( header.getNonce().equals( 1133255010L ) );
            assert( header.getN_tx().equals( 643 ) );



        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    public void TestTransactionParsing() {
        String  path = Paths.get("").toAbsolutePath().toString();
        try {
            Transaction transaction =
                    mapper.readValue(
                            new File(path +"/src/test/resources/TestTransaction.json" ), Transaction.class
                    );

            assert(
                    transaction.getHash().equals( "b6f6991d03df0e2e04dafffcd6bc418aac66049e2cd74b80f14ac86db1e3f0da")
            );

            assert( transaction.getInputs().size() == 1 );
            transaction.getInputs().forEach(
                    input -> {
                        assert( input.getScript().equals( "76a914641ad5051edd97029a003fe9efb29359fcee409d88ac" )  );
                    }
            );

            assert( transaction.getOut().size() == 2 );

        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    public void TestBlockParsing() {
        String  path = Paths.get("").toAbsolutePath().toString();
        try {
            Block block =
                    mapper.readValue(
                            new File( path +"/src/test/resources/TestBlock.json" ), Block.class
                    );

            assert(
                    block.getHash().equals( "0000000000000000014e23da37f3bde8e1a3510bf08915675a1d235c83777c99")
            );
            assert( block.getTx().size() == block.getN_tx() );

        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }
}
