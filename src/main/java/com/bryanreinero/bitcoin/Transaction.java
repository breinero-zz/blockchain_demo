package com.bryanreinero.bitcoin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongodb.morphia.annotations.Id;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 6/5/2016.
 */

public class Transaction {

    private Integer ver;

    @JsonProperty("inputs")
    private Set<Input> inputs = new HashSet<>();
    private Integer block_height;
    private String relayed_by;

    @JsonProperty("out")
    private Set<Output> out = new HashSet<>();
    private Long lock_time;
    private Integer size;
    private Boolean double_spend;
    private Long time;
    private Integer tx_index;
    private Integer vin_sz;

    @Id
    private final String hash;
    private Integer vout_sz;

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    private String blockHash;

    @JsonCreator
    public Transaction( @JsonProperty("hash") String hash ) {
        this.hash = hash;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Set<Input> getInputs() {
        return inputs;
    }

    public void setInputs(Set<Input> inputs) {
        this.inputs = inputs;
    }

    public Integer getBlock_height() {
        return block_height;
    }

    public void setBlock_height(Integer block_height) {
        this.block_height = block_height;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }

    public Set<Output> getOut() {
        return out;
    }

    public void setOut(Set<Output> out) {
        this.out = out;
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

    public Boolean getDouble_spend() {
        return double_spend;
    }

    public void setDouble_spend(Boolean double_spend) {
        this.double_spend = double_spend;
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

    public Integer getVin_sz() {
        return vin_sz;
    }

    public void setVin_sz(Integer vin_sz) {
        this.vin_sz = vin_sz;
    }

    public String getHash() {
        return hash;
    }

    public Integer getVout_sz() {
        return vout_sz;
    }

    public void setVout_sz(Integer vout_sz) {
        this.vout_sz = vout_sz;
    }
}
