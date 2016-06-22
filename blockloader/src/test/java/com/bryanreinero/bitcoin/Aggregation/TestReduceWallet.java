package com.bryanreinero.bitcoin.aggregation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/21/2016.
 */
public class TestReduceWallet {

    @Test
    public void TestReduceFunction() {
        ReduceWalletFunction function = new ReduceWalletFunction();

        Wallet w1 = new Wallet( "Bryan" );
        Wallet w2 = new Wallet( "Bryan" );

        Record r1 = new Record();
        r1.setTx( "1" );
        r1.setTx_Index( 1 );
        r1.setValue( 5L );
        r1.setType( Record.Type.output );

        Record r2 = new Record();
        r2.setTx( "2" );
        r2.setTx_Index( 2 );
        r2.setValue( 10L );
        r2.setType( Record.Type.output );


        Record r3 = new Record();
        r3.setTx( "3" );
        r3.setTx_Index( 1 );
        r3.setValue( 5L );
        r3.setType( Record.Type.input );

        List<Record> records = new ArrayList<>();
        records.add( r1);
        w1.setRecords( records );

        List<Record> records2 = new ArrayList<>();
        records2.add( r2 );
        records2.add( r3 );

        w2.setRecords( records2 );

        Wallet w3 = null;
        try {
            w3 = function.call(w1, w2);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        assert( w3.getRecords().size() == 1 );

        final Long[] total = {0L};
        w3.getRecords().forEach(
                record -> {
                    assert( record.getType() == Record.Type.output );
                    total[0] +=  record.getValue();
                }
        );

        assert( total[0] == 10 );

    }

}
