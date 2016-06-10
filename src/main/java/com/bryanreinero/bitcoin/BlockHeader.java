package com.bryanreinero.bitcoin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.IOException;
import java.util.Set;

/**
 * Created by brein on 5/27/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
public class BlockHeader {

    @Id
    private final String hash;
    private Integer ver;
    private String prev_block;
    private String mrkl_root;
    private Long time;
    private Integer bits;
    private Long fee;
    private Long nonce;
    private Integer n_tx;
    private Integer size;
    private Integer block_index;
    private Boolean main_chain;
    private Integer height;
    private Set<Integer> txIndexes;

    @JsonCreator
    public BlockHeader(@JsonProperty("hash")String hash ) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public String getPrev_block() {
        return prev_block;
    }

    public void setPrev_block(String prev_block) {
        this.prev_block = prev_block;
    }

    public String getMrkl_root() {
        return mrkl_root;
    }

    public void setMrkl_root(String mrkl_root) {
        this.mrkl_root = mrkl_root;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getBits() {
        return bits;
    }

    public void setBits(Integer bits) {
        this.bits = bits;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Integer getN_tx() {
        return n_tx;
    }

    public void setN_tx(Integer n_tx) {
        this.n_tx = n_tx;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getBlock_index() {
        return block_index;
    }

    public void setBlock_index(Integer block_index) {
        this.block_index = block_index;
    }

    public Boolean getMain_chain() {
        return main_chain;
    }

    public void setMain_chain(Boolean main_chain) {
        this.main_chain = main_chain;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Set<Integer> getTxIndexes() {
        return txIndexes;
    }

    public void setTxIndexes(Set<Integer> txIndexes) {
        this.txIndexes = txIndexes;
    }

    public static BlockHeader parseJSON(String json ) throws IOException {
        return ApplicationContext.INSTANCE.getMapper().readValue( json, BlockHeader.class );
    }
}


