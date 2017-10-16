package com.mongodb.blockchain.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by brein on 6/18/2016.
 */
public class BlockChainServletListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext sc = sce.getServletContext();
        String dbHost = sc.getInitParameter("mongoHost");
        String dbPort = sc.getInitParameter("mongoPort");
        sc.setAttribute(
                "mongoclient",
                new MongoClient( new ServerAddress( dbHost, Integer.parseInt(dbPort)) )
        );

        sc.setAttribute( "mapper", new ObjectMapper() );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
