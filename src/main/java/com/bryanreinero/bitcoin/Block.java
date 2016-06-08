package com.bryanreinero.bitcoin;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/27/2016.
 */
public class Block {

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
    private Long received_time;
    private String relayed_by;

    @JsonProperty("tx")
    private Set<Transaction> tx = new HashSet<>();

    @JsonCreator
    public Block( @JsonProperty( "hash" )String hash ) {
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

    public Set<Transaction> getTx() {
        return tx;
    }

    public void setTx(Set<Transaction> tx) {
        this.tx = tx;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }

    public Long getReceived_time() {
        return received_time;
    }

    public void setReceived_time(Long received_time) {
        this.received_time = received_time;
    }

    public BlockHeader getBlockHeader() {

        BlockHeader header = new BlockHeader( hash );
        header.setBits( bits );
        header.setBlock_index( block_index );
        header.setFee( fee );
        header.setHeight( height );
        header.setMain_chain( main_chain );
        header.setMrkl_root( mrkl_root );
        header.setN_tx( n_tx );
        header.setNonce( nonce );
        header.setPrev_block( prev_block );
        header.setSize( size );
        header.setTime( time );
        header.setVer( ver );
        header.setVer( ver );

        return header;
    }
}
