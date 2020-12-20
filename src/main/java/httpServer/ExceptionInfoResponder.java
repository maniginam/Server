package main.java.httpServer;

import main.java.server.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Map<String, Object> response;
    Map<String, String> header;
    private byte[] body;
    private boolean bodyIsSet;
    private String serverName;
    private Map<String, Object> request;

    public ExceptionInfoResponder(String serverName) {
        this.serverName = serverName;
        response = new HashMap<>();
    }

    public Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        setHeader("text/html");
        setResponse(404);
        return response;
    }

    @Override
    public void setHeader(String type) throws IOException {
        if(!bodyIsSet)
            setBody();
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException {
        String message = String.valueOf(request.get("message"));
        bodyIsSet = true;
        body = ("<h1>" + message + "</h1>").getBytes();
    }

    @Override
    public void setResponse(int statusCode) {
        response.put("statusCode", statusCode);
        response.put("headers", header);
        response.put("body", body);
    }
}