package com.mongodb.blockchain.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by brein on 6/25/2016.
 */
public class LineageBuilder {

    static Logger log = Logger.getLogger( LineageBuilder.class.getName() );
    private MongoDatabase db;
    private String  json;
    private ObjectMapper om = new ObjectMapper();

    public LineageBuilder( String filename, MongoDatabase db ) throws IOException {
        json = readFile( filename );
        this.db = db;
    }

    public static String readFile( String path ) throws IOException {

        StringBuffer buf = new StringBuffer();

        RandomAccessFile aFile = new RandomAccessFile (path, "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(inChannel.read(buffer) > 0)
        {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++)
            {
                buf.append((char) buffer.get());
            }
            buffer.clear(); // do something with the data and clear/compact it.
        }
        inChannel.close();
        aFile.close();
        return buf.toString();
    }


    public String getAncestry( String txID ) throws JsonProcessingException {
        String command = json.replaceAll( "txId", txID );
        List<Document> docs = (List<Document>) db.runCommand( Document.parse( command ) ).get("result");

        if( docs == null )
            log.warning( "There's nothing in the lineage" );

        Node parent = new Node();
        parent.setName( txID );
        parent.setChildren(
                reformat( docs, 0 )
        );

        return om.writeValueAsString( parent );
    }

    public List<Node> reformat( List<Document> generations, Integer depth ) {

        List<Node> children = new ArrayList();
        // go back, to the previous generation, one at a time
        // generations are List of documents

        if( depth < generations.size() ) {

            // first get the earlier generation
            List<Node> parents = reformat( generations, depth + 1 );

            // go through each transaction in this generation
            Document transactions = generations.get( depth );
            List<Document> txs = (List<Document>)transactions.get( "transactions" );
            for( int i = 0; i < txs.size(); i++ ) {
                Document document = txs.get( i );
                Node child = new Node();
                child.setChildNames( (List<String>)document.get("toAddr") );
                child.setName( document.getString( "fromtx" ) );
                child.setAddress( document.getString("address") );
                children.add( child );

                // check for paternity
                parents.forEach(
                        parent -> {
                            parent.getChildNames().forEach(
                                    name -> {
                                        if (name.equals(child.getAddress())) {
                                            log.info( "Match: { parent: "+name+", child:"+parent.getAddress()+" } ");
                                            child.addParent(parent);
                                        }
                                        else {
                                            log.info("Miss: { parent: " +name + ", child:" + parent.getAddress() + " } ");
                                        }
                                    }
                            );

                        }
                );
            }
        }
        return children;
    }

}
