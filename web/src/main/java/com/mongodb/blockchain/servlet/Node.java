package com.mongodb.blockchain.servlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/23/2016.
 */
public class Node {
    String name;
    String address;
    Long satoshi;
    List<Node> children = new ArrayList<>();
    List<String> childNames = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSatoshi() {
        return satoshi;
    }

    public void setSatoshi(Long satoshi) {
        this.satoshi = satoshi;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<String> getChildNames() {
        return childNames;
    }

    public void setChildNames(List<String> childNames) {
        this.childNames = childNames;
    }

    public void addParent( Node child ) {
        children.add( child );
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
