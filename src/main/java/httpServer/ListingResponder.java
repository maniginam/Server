package main.java.httpServer;

import main.java.server.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListingResponder implements Responder {
    private final String root;
    private final String serverName;
    Map<String, Object> response;
    Map<String, String> header;
    private byte[] body;
    private Map<String, Object> request;

    public ListingResponder(String serverName, String root) {
        this.root = root;
        this.serverName = serverName;
        response = new HashMap<>();
    }

    public Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        setHeader("text/html");
        setResponse(200);
        return response;
    }

    @Override
    public void setHeader(String type) throws IOException {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException, ExceptionInfo {
        File directory;
        String p = "";
        if (String.valueOf(request.get("resource")).contains("img")) {
            directory = new File(root + "/img");
            p = "/img";
        } else
            directory = new File(root);
        File[] files = directory.listFiles();
        String bodyMsg = "<ul>";
        for (File file : files) {
            if (file.isDirectory()) {
                String pd = "/listing";
                bodyMsg = bodyMsg + "<li><a href=\"" + pd + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
            }
            else
                bodyMsg = bodyMsg + "<li><a href=\"" + p + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
        }
        bodyMsg = bodyMsg + "</ul>";
        body = bodyMsg.getBytes();
    }

    @Override
    public void setResponse(int statusCode) {
        response.put("statusCode", statusCode);
        response.put("headers", header);
        response.put("body", body);
    }
}