package com.bryanreinero.bitcoin.aggregation;

import org.apache.spark.api.java.function.Function2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/21/2016.
 */
public class ReduceWalletFunction implements Function2<Wallet, Wallet, Wallet> {

    @Override
    public Wallet call(Wallet w1, Wallet w2) throws Exception {
        List<Record> mergedRecords = new ArrayList<>();

        List<Integer> w1DeadPool = new ArrayList();
        List<Integer> w2DeadPool = new ArrayList();

        w1.getRecords().forEach(
                record1 -> {
                    w2.getRecords().forEach(
                            record2 -> {
                                if( record1.getTx_Index().equals( record2.getTx_Index() ) ) {
                                    if (record1.getType() == Record.Type.input
                                            && record2.getType() == Record.Type.output )
                                        w2DeadPool.add( record1.getTx_Index() );

                                    if (record1.getType() == Record.Type.output
                                            && record2.getType() == Record.Type.input)
                                        w1DeadPool.add( record1.getTx_Index() );
                                }
                            }
                    );
                }
        );

        w1DeadPool.forEach(
                integer -> { w1.matchAndRemoveOutput( integer ) ;}
        );

        w2DeadPool.forEach(
                integer -> { w2.matchAndRemoveOutput( integer ) ;}
        );

        // merge what's left over
        w1.getRecords().forEach(
                record -> {
                    if( record.getType() == Record.Type.output )
                        mergedRecords.add( record );
                }
        );
        w2.getRecords().forEach(
                record -> {
                    if( record.getType() == Record.Type.output )
                        mergedRecords.add( record );
                }
        );

        Wallet wallet = new Wallet( w1.getAddress() );
        wallet.setRecords( mergedRecords );
        return wallet;
    }
}
