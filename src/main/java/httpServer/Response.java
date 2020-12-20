package main.java.httpServer;

import java.util.Map;

public interface Response extends Runnable {
    Map<String, Object> buildResponse();

    void stop() throws InterruptedException;

    void hasGivenResponse(boolean b);

    boolean hasResponded();
}
