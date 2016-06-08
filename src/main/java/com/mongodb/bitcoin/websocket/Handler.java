package com.mongodb.bitcoin.websocket;

/**
 * Created by brein on 5/15/2016.
 */
public interface Handler <T> {

    void Handle( final String msg ) throws Exception;
    String getName();
    String getMessage();
}
