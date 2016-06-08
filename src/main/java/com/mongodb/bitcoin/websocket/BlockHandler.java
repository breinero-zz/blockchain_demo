package com.mongodb.bitcoin.websocket;

import com.bryanreinero.bitcoin.ApplicationContext;
import com.bryanreinero.bitcoin.BlockHeader;

import java.io.IOException;


/**
 * Created by brein on 5/15/2016.
 */
public class BlockHandler implements Handler {

    private final String op = "block";
    private final String message = "{\"op\":\"blocks_sub\"}"; //"{\"op\":\"ping_block\"}";
    private final Consumer<BlockHeader> consumer;

    public BlockHandler( Consumer<BlockHeader> consumer ) {
        this.consumer = consumer;
    }

    @Override
    public void Handle( String msg ) throws Exception {
        try {
            consumer.consume(
                    ApplicationContext.INSTANCE.getMapper().readValue(msg, BlockHeader.class)
            );

        } catch ( IOException e ) {
            throw new Exception( "Can't handle received block ", e );
        }
    }

    @Override
    public String getName() {
        return op;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
