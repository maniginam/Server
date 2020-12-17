package server;

import httpServer.HttpResponseBuilder;

public interface Connection extends Runnable {
    void start();
    void stop() throws InterruptedException;

    HttpResponseBuilder getResponseBuilder();

    Thread getThread();

    Router getRouter();
}
