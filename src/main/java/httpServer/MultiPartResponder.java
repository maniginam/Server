package httpServer;

import server.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiPartResponder implements Responder {
    private final String serverName;
    private final Map<String, Object> response;
    private Map<String, Object> request;
    private Map<String, String> header;
    private byte[] body;
    private String fileName;
    private String type;
    private boolean responding;
    private Thread respond;

    public MultiPartResponder(String serverName) {
        this.serverName = serverName;
        response = new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo {
        responding = true;
        this.request = request;
        type = "application/octet-stream";
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
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException {
        String bodyMsg = "<h2>POST Form</h2>" +
                "<li>file name: " + String.valueOf(request.get("fileName")).replace("\"", "") + "</li>" +
                "<li>file size: " + request.get("fileSize") + "</li>" +
                "<li>content type: " + type + "</li>";
        body = bodyMsg.getBytes();
    }

    @Override
    public void run() {
        try {
            setBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
            setHeader(type);
        setResponse(200);
    }

    @Override
    public void stop() throws InterruptedException {
        if (respond != null)
            respond.join();
    }
}
