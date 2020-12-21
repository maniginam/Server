package httpServer;

import server.Responder;

import java.util.HashMap;
import java.util.Map;

public class FormResponder implements Responder {
    private final String serverName;
    private final Map<String, Object> response;
    private Map<String, Object> request;
    private Map<String, String> header;
    private byte[] body;
    private boolean responding;
    private Thread respond;

    public FormResponder(String serverName) {
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
    public void setHeader(String type) {
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
        for (int i = 1; i < entries.length; i = i + 2) {
            bodyMsg = bodyMsg + "<li>" +
                    entries[i] + ": " + entries[i + 1] +
                    "</li>";
        }
        body = bodyMsg.getBytes();
    }

    @Override
    public void run() {
        setBody();
        setHeader("text/html");
        setResponse(200);
    }

    @Override
    public void stop() throws InterruptedException {
        if (respond != null)
            respond.join();
    }
}
