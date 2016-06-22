package com.bryanreinero.bitcoin;

/**
 * Created by brein on 6/5/2016.
 */
public class Input {

    private Long sequence;
    private Output prev_out;
    private String script;
    private String blockHash;
    private String txID;

    private Integer block_height;
    private Long lock_time;
    private Integer size;
    private Long time;
    private Integer tx_index;

    private Integer vout_sz;


    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Output getPrev_out() {
        return prev_out;
    }

    public void setPrev_out(Output prev_out) {
        this.prev_out = prev_out;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Integer getBlock_height() {
        return block_height;
    }

    public void setBlock_height(Integer block_height) {
        this.block_height = block_height;
    }

    public Long getLock_time() {
        return lock_time;
    }

    public void setLock_time(Long lock_time) {
        this.lock_time = lock_time;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getTx_index() {
        return tx_index;
    }

    public void setTx_index(Integer tx_index) {
        this.tx_index = tx_index;
    }

    public Integer getVout_sz() {
        return vout_sz;
    }

    public void setVout_sz(Integer vout_sz) {
        this.vout_sz = vout_sz;
    }
}
