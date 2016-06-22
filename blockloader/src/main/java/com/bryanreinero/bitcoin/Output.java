package com.bryanreinero.bitcoin;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by brein on 6/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Output {

    private String hash;
    private Boolean spent;
    private Integer tx_index;
    private Integer type;
    private String addr;
    private Long value;
    private Integer n;
    private String script;
    private String txID;
    private String blockHash;
    private Integer blockHeight;

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getSpent() {
        return spent;
    }

    public void setSpent(Boolean spent) {
        this.spent = spent;
    }

    public Integer getTx_index() {
        return tx_index;
    }

    public void setTx_index(Integer tx_index) {
        this.tx_index = tx_index;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
