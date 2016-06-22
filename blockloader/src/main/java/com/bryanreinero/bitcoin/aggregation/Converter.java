package com.bryanreinero.bitcoin.aggregation;

import com.bryanreinero.bitcoin.Input;
import com.bryanreinero.bitcoin.Transaction;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/22/2016.
 */
public class Converter {

    public static Transaction map( Document d ) {
        Transaction t = new Transaction();
        t.setBlock_height( d.getInteger( "block_height" ) );
        t.setBlock_height( d.getInteger( "replayed by" ) );
        t.setLock_time( d.getLong( "lock time" ) );
        t.setSize( d.getInteger( "size" ) );
        t.setDouble_spend( d.getBoolean( "double_spend" ) );
        t.setTime( d.getLong("time"));
        t.setTx_index( d.getInteger( "tx_index") );
        t.setVer( d.getInteger("vin_sz ") );
        t.setInputs((List<Input>) d.get("inputs"));

        return t;
    }

    public static List<Input> map( List<Document> docs ) {
        List<Input> inputs = new ArrayList();

        docs.forEach(
                d -> {
                    Input input = new Input();
                    //private Long sequence;
                    input.setSequence( d.getLong( "sequence" ) );


                    //private Output prev_out;

                    input.setScript( d.getString( "script" ));
                    //private String blockHash;
                    input.setBlockHash( d.getString( "blockHash" ) );
                    // private String txID;
                    input.setTxID( d.getString( "txID" ) );
                    // private Integer block_height;
                    input.setBlock_height( d.getInteger("block_hieght"));
                    // private Long lock_time;
                    input.setLock_time( d.getLong( "lock_time" ));
                    // private Integer size;
                    input.setSize( d.getInteger("size"));
                    // private Long time;
                    input.setTime( d.getLong("time"));
                    // private Integer tx_index;
                    input.setTx_index( d.getInteger("tx_index"));
                    // private Integer vout_sz;
                    input.setVout_sz( d.getInteger("vout_sz"));

                    inputs.add( input );
                }
        );
        return inputs;
    }


}
