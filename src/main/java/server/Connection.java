package server;

import java.util.Map;

public interface Connection extends Runnable {
    void start();
    void stop() throws InterruptedException;
    Thread getThread();
    Router getRouter();

    Map<String, Object> getResponseMap();
}
