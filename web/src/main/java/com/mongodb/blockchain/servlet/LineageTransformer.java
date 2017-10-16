package com.mongodb.blockchain.servlet;

import org.bson.Document;


import java.util.List;

/**
 *  This class transformas the list of anscestor transactions
 *  from the mongodb $graphLooup command and formats it into a nested
 *  hierarchy of generations
 * Created by brein on 6/19/2016.
 */
public class LineageTransformer {

    private class Tx{
        String addr;
        String tranaction;
        Long value;
        List<Tx> parents;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getTranaction() {
            return tranaction;
        }

        public void setTranaction(String tranaction) {
            this.tranaction = tranaction;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public List<Tx> getParents() {
            return parents;
        }

        public void setParents(List<Tx> children) {
            this.parents = children;
        }
    }

    public static Tx transform( Tx tx, List<Document> rawTxs, Integer depth) {
        Tx node = null;

        List<Document> parentNodes = (List) rawTxs.get( depth );

        return node;
    }
}
