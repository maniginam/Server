package httpServer;

import server.Responder;

import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Map<String, Object> response;
    Map<String, String> header;
    private byte[] body;
    private boolean bodyIsSet;
    private String serverName;
    private Map<String, Object> request;
    private boolean responding;
    private Thread respond;

    public ExceptionInfoResponder(String serverName) {
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
        if (!bodyIsSet)
            setBody();
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() {
        String message = String.valueOf(request.get("message"));
        bodyIsSet = true;
        body = ("<h1>" + message + "</h1>").getBytes();
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
    public void stop() throws InterruptedException {
        if (respond != null)
            respond.join();
    }

    @Override
    public void run() {
        setBody();
        setHeader("text/html");
        setResponse(200);
    }
}