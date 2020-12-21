package httpServer;

import server.ExceptionInfo;
import server.Responder;

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
    private boolean responding;
    private Thread respond;

    public ListingResponder(String serverName, String root) {
        this.root = root;
        this.serverName = serverName;
        response = new HashMap<>();
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) {
        responding = true;
        this.request = request;
        respond = new Thread(this);
        respond.start();
        return response;
    }

    @Override
    public void setHeader(String type) {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException, ExceptionInfo {
        File directory;
        String resource = String.valueOf(request.get("resource"));
        String p = "";
        if (resource.contains("img")) {
            directory = new File(root + "/img");
            p = "/img";
        } else if (resource.contains("/listing"))
            directory = new File(root);
        else {
            throw new ExceptionInfo("Target Resource is 93 million miles away");
        }
        File[] files = directory.listFiles();
        String bodyMsg = "<ul>";
        for (File file : files) {
            if (file.isDirectory()) {
                String pd = "/listing";
                bodyMsg = bodyMsg + "<li><a href=\"" + pd + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
            } else
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
        responding = false;
    }

    @Override
    public boolean isResponding() {
        return responding;
    }

    @Override
    public Map<String, Object> getResponse() {
        return response;
    }

    @Override
    public void run() {
        try {
            setBody();
        } catch (IOException | ExceptionInfo e) {
            e.printStackTrace();
        }
        setHeader("text/html");
        setResponse(200);
    }

    @Override
    public void stop() throws InterruptedException {
        if (respond != null)
            respond.join();
    }
}