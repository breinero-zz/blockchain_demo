package com.mongodb.blockchain.servlet;

import javax.servlet.ServletContextListener;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


/**
 * Created by brein on 6/18/2016.
 */
public class BlockchainServletListner implements ServletContextListener {

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {

        ServletContext sc = event.getServletContext();

        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        String dbHost = sc.getInitParameter("mongoHost");
        String dbPort = sc.getInitParameter("mongoPort");
        sc.setAttribute(
                "mongoclient",
                new MongoClient( new ServerAddress( dbHost, Integer.parseInt(dbPort)), options )
        );

        SampleSet stats = new SampleSet();
        //stats.start();
        sc.setAttribute( "stats", stats );
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
}
