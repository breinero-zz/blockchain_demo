package com.mongodb.bitcoin.websocket;

import com.bryanreinero.bitcoin.ApplicationContext;
import com.bryanreinero.bitcoin.Transaction;

import java.io.IOException;

/**
 * Created by brein on 5/15/2016.
 */
public class TransactionHandler implements Handler {

    private final Consumer<Transaction> consumer;

    public TransactionHandler(Consumer<Transaction> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void Handle( String msg) throws Exception {
        try {
            consumer.consume(
                    ApplicationContext.INSTANCE.getMapper().readValue(msg, Transaction.class)
            );
        } catch ( IOException e ) {
            throw new Exception( "Can't handle received transaction", e );
        }
    }

    @Override
    public String getName() {
        return "utx";
    }

    @Override
    public String getMessage() {
        return "{\"op\":\"unconfirmed_sub\"}";
    }
}
