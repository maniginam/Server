package main.java.httpServer;

import main.java.server.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormResponder implements Responder {
    private final String serverName;
    private final Response response;
    private Request request;
    private Map<String, String> header;
    private byte[] body;

    public FormResponder(String serverName) {
        this.serverName = serverName;
        response = new Response();
    }

    @Override
    public Response respond(Request request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        setHeader("text/html");
        setResponse(200);
        return response;
    }

    @Override
    public void setResponse(int statusCode) {
        response.put("statusCode", statusCode);
        response.put("headers", header);
        response.put("body", body);
    }

    @Override
    public void setHeader(String type) throws IOException, ExceptionInfo {
        if (body == null) {
            setBody();
        }
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() {
        String[] entries = String.valueOf(request.get("resource")).split("[?=&]");

        String bodyMsg = "<h2>GET Form</h2>";
        for (int i = 1; i < entries.length; i= i+2) {
            bodyMsg = bodyMsg + "<li>" +
                    entries[i] + ": " + entries[i+1] +
                    "</li>";
        }
        body = bodyMsg.getBytes();
    }
}
