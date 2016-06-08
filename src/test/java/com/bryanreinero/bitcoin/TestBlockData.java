package com.bryanreinero.bitcoin;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by brein on 6/5/2016.
 */
public class TestBlockData {

    ClassLoader loader = this.getClass().getClassLoader();
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testInputParsing() {
        try {
            Input input =
                    mapper.readValue(
                            new File( "C:\\Users\\brein\\IdeaProjects\\blockchain_demo\\build\\resources\\test" +
                                    "\\TestInput.json" ), Input.class
                    );


            assert( input.getPrev_out().getSpent().equals( true ) );
            assert( input.getPrev_out().getAddr().equals( "1MHAHHpueH3hhBzKfpDuNxR7oDWPEg7jVL" ) );

        } catch (IOException e) {
            e.printStackTrace();
            assert( false );
        }
    }

    @Test
    public void testOutputParsing() {
        try {

            Output output =
                mapper.readValue(
                            new File( "C:\\Users\\brein\\IdeaProjects\\blockchain_demo\\build\\resources\\test" +
                                    "\\TestOutput.json" ), Output.class
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
        try {
            BlockHeader header =
                    mapper.readValue(
                            new File( "C:\\Users\\brein\\IdeaProjects\\blockchain_demo\\build\\resources\\test" +
                                    "\\TestBlock.json" ), BlockHeader.class
                    );


            assert( header.getHash().equals( "0000000000000bae09a7a393a8acded75aa67e46cb81f7acaa5ad94f9eacd103" ) );
            assert( header.getVer().equals( 1 ) );
            assert(
                    header.getPrev_block().equals( "00000000000007d0f98d9edca880a6c124e25095712df8952e0439ac7409738a" )
            );
            assert( header.getTime().equals( 1322131230L ) );
            assert(
                    header.getMrkl_root().equals( "935aa0ed2e29a4b81e0c995c39e06995ecce7ddbebb26ed32d550a72e8200bf5")
            );
            assert( header.getFee().equals( 200000L ) );
            assert( header.getBits().equals( 437129626 ) );
            assert( header.getNonce().equals( 2964215930L ) );
            assert( header.getN_tx().equals( 22 ) );



        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    public void TestTransactionParsing() {
        try {
            Transaction transaction =
                    mapper.readValue(
                            new File( "C:\\Users\\brein\\IdeaProjects\\blockchain_demo\\build\\resources\\test" +
                                    "\\TestTransaction.json" ), Transaction.class
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
        try {
            Block block =
                    mapper.readValue(
                            new File( "C:\\Users\\brein\\IdeaProjects\\blockchain_demo\\build\\resources\\test" +
                                    "\\TestBlock.json" ), Block.class
                    );

            assert(
                    block.getHash().equals( "0000000000000bae09a7a393a8acded75aa67e46cb81f7acaa5ad94f9eacd103")
            );
            assert( block.getTx().size() == block.getN_tx() );

        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }
}
