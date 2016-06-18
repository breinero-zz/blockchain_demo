package com.mongodb.blockchain.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by brein on 6/18/2016.
 */
public class CoinInFlightServlet extends HttpServlet {

    private String name;

    @Override
    public void init(ServletConfig config) throws ServletException {

        name = config.getServletName();

        final MongoClient client = (MongoClient) config.getServletContext().getAttribute( "mongoclient" );
        final MongoDatabase db = client.getDatabase( config.getInitParameter( "db" ) );

        replay = new LogReplayer(
                db.getCollection( config.getInitParameter( "collection" ), TaxiLog.class )
        );

        final MongoCollection<Document> snapshots = db.getCollection( config.getInitParameter( "snapshots" ) ) ;
        loadSnapOpName = config.getInitParameter( "snapshotOperationName" );
        final Integer snapshotInterval = Integer.parseInt( config.getInitParameter( "snapshotInterval" ) );

        samples = (SampleSet) config.getServletContext().getAttribute( "stats" );


        Class<?> builderClazz = null;
        try {
            builderClazz = Class.forName( config.getInitParameter("SnapBuilderClassName") );
            ctor = builderClazz.getConstructor(Snapshot.class);

            Method convertMethod = Class.forName(
                    config.getInitParameter("ConverterClassName")
            ).getDeclaredMethod("convert", Document.class);

            //Instanciate snapshot cache
            cache = new SnapshotCache(
                    snapshots,
                    snapshotInterval,
                    convertMethod
            );
        } catch (ClassNotFoundException | NoSuchMethodException e ) {
            throw new ServletException( name+" servlet could not initialize.", e );
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Get the requested time
        String ts = req.getParameter( "t" );
        Integer requestedTS ;
        if( ts != null )
            requestedTS = Integer.parseInt(ts);
        else
            requestedTS = Math.toIntExact(System.currentTimeMillis() / 1000);

        try {
            Snapshot snapshot = null;
            try ( Interval x = samples.set( loadSnapOpName ) ) {
                snapshot = cache.getSnapshot(requestedTS);
            }
            ViewBuilder agg = (ViewBuilder) ctor.newInstance( snapshot );
            replay.addBuilder(agg);

            Integer start = 0;
            if ( snapshot != null)
                start = snapshot.getEndTime();

            try ( Interval x = samples.set("logReplay") ) {
                replay.replayLogs(start, requestedTS );
            }

            resp.setContentType("application/json");
            resp.getWriter().println(agg.getView());
        } catch (IllegalAccessException  | InstantiationException | InvocationTargetException e) {
            throw new IOException( name+"can not service request", e  );
        }
    }
}
