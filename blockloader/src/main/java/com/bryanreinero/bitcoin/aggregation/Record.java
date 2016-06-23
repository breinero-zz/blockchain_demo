package com.bryanreinero.bitcoin.aggregation;

import java.io.Serializable;

/**
 * Created by brein on 6/19/2016.
 */
public class Record implements Serializable {

    public enum Type{ input, output };

    String tx;
    Long value;
    Type type;
    Integer tx_Index;

    public Integer getTx_Index() {
        return tx_Index;
    }

    public void setTx_Index(Integer tx_Index) {
        this.tx_Index = tx_Index;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
