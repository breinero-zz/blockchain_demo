package com.mongodb.blockchain.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Created by brein on 6/18/2016.
 */
public class TransactionServlet extends HttpServlet {
    static Logger log = Logger.getLogger( TransactionServlet.class.getName() );

    private String name;
    private MongoCollection<Document> transactions;
    private ObjectWriter ow;

    @Override
    public void init(ServletConfig config) throws ServletException {

        name = config.getServletName();

        final MongoClient client = (MongoClient) config.getServletContext().getAttribute( "mongoclient" );
        transactions = client.getDatabase( config.getInitParameter( "db" ) )
                .getCollection( config.getInitParameter( "collection"  ) );

        final ObjectMapper mapper = (ObjectMapper) config.getServletContext().getAttribute( "mapper" );
        ow =  mapper.writer().withDefaultPrettyPrinter();

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        boolean error = false;

        String path = req.getRequestURI();
        String tx = path.substring( path.lastIndexOf( "/" ) +1 );
        Document doc = null;
        try {
            doc = transactions.find( new Document( "_id", tx ) ).limit(1).first() ;
            if( doc != null )
                resp.getWriter().print( ow.writeValueAsString( doc ) ) ;
            else
                resp.sendError( SC_NOT_FOUND );
        } catch ( Exception e) {
            e.printStackTrace();
            log.warning( "failed to serve transaction request for "+tx );
            log.warning( "Cant print response, for reason " );
            resp.sendError( SC_INTERNAL_SERVER_ERROR );
        }
    }
}
