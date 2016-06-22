package com.bryanreinero.bitcoin.aggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 6/19/2016.
 */
public class Wallet {

    private final String address;
    private List<Record> records = new ArrayList<>();

    public Wallet(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }


    public void matchAndRemoveOutput( Integer id ) {

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);

            if (record.getTx_Index().equals(id) && record.getType() == Record.Type.output)
                records.remove(i);
        }
    }
}
