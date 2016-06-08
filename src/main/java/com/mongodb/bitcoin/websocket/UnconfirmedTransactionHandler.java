package com.mongodb.bitcoin.websocket;

import com.bryanreinero.bitcoin.ApplicationContext;
import com.bryanreinero.bitcoin.Transaction;

import java.io.IOException;

/**
 * Created by brein on 5/15/2016.
 */
public class UnconfirmedTransactionHandler implements Handler {

    private final String name = "utx";
    private final String message = "{\"op\":\"unconfirmed_sub\"}";
    private final Consumer<Transaction> consumer;

    public UnconfirmedTransactionHandler(Consumer<Transaction> consumer) {
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
        return name;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
