package main.java.server;

import main.java.httpServer.*;

public interface Connection extends Runnable {
    void start();
    void stop() throws InterruptedException;

    HttpResponseBuilder getResponseBuilder();

    Thread getThread();

    Router getRouter();
}
