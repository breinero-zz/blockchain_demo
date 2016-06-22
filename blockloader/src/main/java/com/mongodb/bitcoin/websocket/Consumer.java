package com.mongodb.bitcoin.websocket;

/**
 * Created by brein on 6/5/2016.
 */
public interface Consumer <T> {
    void consume( T t );
}
