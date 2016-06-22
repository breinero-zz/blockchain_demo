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
                    transaction.getHash().equals( "7d9e8a48dd3838feff2dad1b971776bd56132078f16084c2a905dcd261e6a17f")
            );

            assert( transaction.getInputs().size() == 1 );
            transaction.getInputs().forEach(
                    input -> {
                        assert( input.getScript().equals( "47304402205b59ed2aa07e37e8d5286252460865c78acb63d93665a6050346131746bcdb1902205df1039710953fbbbe04ea50b9f9f46ffe4d94d9ed4b6a8b5148f6d732c320cc012103bc674fe81deb77b80c19751ccfb51f7d5f7636783b13b7781fe49d3d04e6befe" )  );
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
