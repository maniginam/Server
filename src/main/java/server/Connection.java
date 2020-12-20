package main.java.server;

public interface Connection extends Runnable {
    void start();
    void stop() throws InterruptedException;
    Thread getThread();
    Router getRouter();
}
