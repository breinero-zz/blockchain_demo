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
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * Created by brein on 6/18/2016.
 */
public class LineageServlet extends HttpServlet {

    ObjectMapper om = new ObjectMapper();

    static Logger log = Logger.getLogger( LineageServlet.class.getName() );

    private String servletName = "TransactionLineage";
    private MongoCollection<Document> transactions;
    private MongoDatabase db;
    private LineageBuilder  builder;


    @Override
    public void init(ServletConfig config) throws ServletException {

        servletName = config.getServletName();

        final MongoClient client = (MongoClient) config.getServletContext().getAttribute( "mongoclient" );
        db = client.getDatabase( config.getInitParameter( "db" ) );
        String commandFile = config.getServletContext().getRealPath("/WEB-INF/aggregations/" + servletName + ".json" );

        try {
            builder = new LineageBuilder( commandFile, db );
        } catch (IOException x) {
            String err = "Failed to read command file "+commandFile;
            log.severe( err );
            throw new ServletException( err, x );
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String txID = req.getParameter( "tx" );

        resp.setContentType("application/json");
        resp.setHeader( "Access-Control-Allow-Origin", "*");
        try {
            resp.getWriter().print( builder.getAncestry( txID ) );
        } catch (IOException e) {
            log.warning( "Cant print response, for reason "+e.getMessage() );
            resp.sendError( SC_INTERNAL_SERVER_ERROR  );
        }
    }
}
