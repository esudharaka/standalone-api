package com.programming;

import com.programming.controller.AccountController;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {

    private static Server server;
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);
    private final int maxThreads = 100;
    private final int minThreads = 10;
    private final int idleTimeout = 120;

    public static void main(String[] args) {
        LOGGER.info("============ Starting the Server =============");
        JettyServer jettyServer = new JettyServer();
        jettyServer.startServer();
    }


    public void startServer() {
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
        server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);

        server.setConnectors(new Connector[] { connector });

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(AccountController.class, "/account/*");
        try {
            server.start();
        } catch (Exception e) {
            LOGGER.error("Error while starting the server", e);
        }
    }

    public void stop() throws Exception {
        server.stop();
    }
}
