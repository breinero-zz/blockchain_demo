package com.mongodb.blockchain.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * Created by brein on 6/18/2016.
 */
public class LineageServlet extends HttpServlet {

    ObjectMapper om = new ObjectMapper();

    static Logger log = Logger.getLogger( LineageServlet.class.getName() );

    private String servletName;
    private MongoCollection<Document> transactions;
    private MongoDatabase db;
    private String  json;


    private String readFile( String path ) throws IOException {

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

    @Override
    public void init(ServletConfig config) throws ServletException {

        servletName = config.getServletName();

        final MongoClient client = (MongoClient) config.getServletContext().getAttribute( "mongoclient" );
        db = client.getDatabase( config.getInitParameter( "db" ) );

        String commandFile =  config.getServletContext().getRealPath("/WEB-INF/aggregations/" + servletName + ".json" );
        try {
            json = readFile( commandFile );
        } catch (IOException x) {
            String err = "Failed to read command file "+commandFile;
            log.severe( err );
            throw new ServletException( err, x );
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setHeader( "Access-Control-Allow-Origin", "*");
        boolean error = false;

        String txID = req.getParameter( "tx" );
        json = json.replaceAll( "txId", txID );

        List<Document> docs = (List<Document>) db.runCommand( Document.parse( json ) ).get("resuls");
        Node parent = new Node();
        if( docs == null )
            log.warning( "There's nothing in the lineage" );

        parent.setChildren( reformat(
                (List<Document>) db.runCommand( Document.parse( json ) ).get("result"),
                parent,
                0
        ));
        parent.setName( txID );

        try {
            resp.getWriter().print(
                    om.writeValueAsString( parent )
            );
        } catch (IOException e) {
            log.warning( "Cant print response, for reason "+e.getMessage() );
            error = true;
        }

        if( error )
            resp.sendError( SC_INTERNAL_SERVER_ERROR  );

    }

    public List<Node> reformat( List<Document> generations, Node child, Integer depth ) {

        List<Node> parents = new ArrayList();
        // go back, to the previous generation, one at a time
        // generations are List of documents

        if( depth < generations.size() ) {
            // go through each transaction in this generation
            Document transactions = generations.get( depth );
            List<Document> txs = (List<Document>)transactions.get( "transactions" );
            for( int i = 0; i < txs.size(); i++ ) {
                Document document = txs.get( i );
                Node parent = new Node();
                parent.setChildNames( (List<String>)document.get("toAddr") );
                parent.setName( document.getString( "fromtx" ) );
                parents.add( parent );

                // check for paternity
                for (String name : parent.getChildNames() ) {
                    if( name.equals( child.getAddress() ) ) {
                        //log.info( "Match: { parent: "+name+", child:"+parent.getAddress()+" } ");
                        child.addParent( parent );
                    }
                    //else {
                    //    log.info("Miss: { parent: " +name + ", child:" + parent.getAddress() + " } ");
                    //}
                }
                reformat( generations, parent, ( depth +1 ) );
            }
        }
        return parents;
    }
}
