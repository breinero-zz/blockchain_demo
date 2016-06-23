package com.bryanreinero.bitcoin.aggregation;

import com.bryanreinero.bitcoin.Input;
import com.bryanreinero.bitcoin.Output;
import com.bryanreinero.bitcoin.Transaction;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/22/2016.
 */
public class Converter {

    public static Transaction mapTx( Document d ) {
        Transaction t = new Transaction( d.getString( "_id" ) );
        t.setBlock_height( d.getInteger( "block_height" ) );
        t.setBlockHash( d.getString( "blockHash") );
        t.setRelayed_by( d.getString( "replayed_by" ) );
        t.setLock_time( d.getLong( "lock_time" ) );
        t.setSize( d.getInteger( "size" ) );
        t.setDouble_spend( d.getBoolean( "double_spend" ) );
        t.setTime( d.getLong("time"));
        t.setTx_index( d.getInteger( "tx_index") );
        t.setVer( d.getInteger("ver") );
        t.setVin_sz( d.getInteger( "vin_sz" ) );
        t.setInputs(  mapInputs( (List<Document>) d.get("inputs" ) ) );
        t.setOut( mapOutputs( (List<Document>) d.get( "out" ) ) );
        return t;
    }
    public static List<Input> mapInputs( List<Document> docs ) {
        List<Input> inputs = new ArrayList<>();
        docs.forEach(
                document -> {
                    inputs.add( mapInput( document ) );
                }
        );
        return inputs;
    }

    public static Input mapInput( Document d ) {

        Input input = new Input();
        input.setSequence( d.getLong( "sequence" ) );
        input.setPrev_out( mapOutput( (Document)d.get("prev_out") ) );
        input.setScript( d.getString( "script" ));
        input.setBlockHash( d.getString( "blockHash" ) );
        input.setTxID( d.getString( "txID" ) );
        input.setBlock_height( d.getInteger("block_hieght"));
        input.setLock_time( d.getLong( "lock_time" ));
        input.setSize( d.getInteger("size"));
        input.setTime( d.getLong("time"));
        input.setTx_index( d.getInteger("tx_index"));
        input.setVout_sz( d.getInteger("vout_sz"));

        return input;
    }

    public static List<Output> mapOutputs( List<Document> docs ) {
        List<Output> outputs = new ArrayList<>();
        docs.forEach(
                document -> {
                    outputs.add( mapOutput( document ) );
                }
        );
        return outputs;
    }

    public static  Output mapOutput( Document d ) {
        Output output = new Output();
        //output.setHash( d.getString( "hash" ) );
        //output.setSpent( d.getBoolean( "spent" ) );
        output.setTx_index( d.getInteger( "tx_index" ) );
        //output.setType( d.getInteger( "type" ) );
        output.setAddr( d.getString( "addr" ) );
        output.setValue( d.getLong( "value" ) );
        //output.setN( d.getInteger( "n" ) );
        //output.setScript( d.getString( "script" ) );
        //output.setTxID( d.getString( "txID" ) );
        //output.setBlockHash( d.getString( "blockHash" ) );
        //output.setBlockHeight( d.getInteger( "blockHeight" ) );

        return output;
    }


}
