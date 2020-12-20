package main.java.server;

import java.io.IOException;
import java.util.Map;

public interface Responder {

    Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo, InterruptedException;

    void setHeader(String type) throws IOException, ExceptionInfo, InterruptedException;

    void setBody() throws IOException, ExceptionInfo, InterruptedException;

    void setResponse(int statusCode);
}