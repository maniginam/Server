package server;

import java.io.IOException;
import java.util.Map;

public interface Responder extends Runnable {

    Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo, InterruptedException;

    void setHeader(String type) throws IOException, ExceptionInfo, InterruptedException;

    void setBody() throws IOException, ExceptionInfo, InterruptedException;

    void setResponse(int statusCode);

    boolean isResponding();

    Map<String, Object> getResponse();

    void stop() throws InterruptedException;
}