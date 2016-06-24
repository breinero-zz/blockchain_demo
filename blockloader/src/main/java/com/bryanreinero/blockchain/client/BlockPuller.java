package com.bryanreinero.blockchain.client;

import java.util.logging.Logger;

/**
 * Created by brein on 6/23/2016.
 */
public class BlockPuller {

    static Logger log = Logger.getLogger( BlockPuller.class.getName() );

    public static void main ( String[] args ) {
        BlockMaintainer maintainer = new BlockMaintainer();
        try {
            maintainer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
