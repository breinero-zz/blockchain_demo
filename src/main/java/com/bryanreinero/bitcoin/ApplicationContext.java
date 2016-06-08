package com.bryanreinero.bitcoin;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by brein on 6/5/2016.
 */
public enum ApplicationContext {

    INSTANCE;

    private final ObjectMapper mapper = new ObjectMapper();

    public ApplicationContext get() {
        return INSTANCE;
    }
    public ObjectMapper getMapper() {
        return mapper;
    }
}
