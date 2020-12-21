package httpServer;

import server.Responder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileResponder implements Responder {
    private final String root;
    private final String serverName;
    private final HashMap<String, String> types;
    HashMap<String, Object> response;
    Map<String, String> header;
    private byte[] body;
    public Map<String, Object> request;
    private boolean responding;
    private Thread respond;

    public FileResponder(String serverName, String root) {
        this.root = root;
        this.serverName = serverName;
        response = new HashMap<>();
        types = new HashMap<>();
        types.put("html", "text/html");
        types.put("pdf", "application/pdf");
        types.put("jpg", "image/jpeg");
        types.put("jpeg", "image/jpeg");
        types.put("png", "image/png");
    }

    public Map<String, Object> respond(Map<String, Object> request) throws IOException {
        responding = true;
        this.request = request;
        respond = new Thread(this);
        respond.start();
        return response;
    }

    private String determineFileType() {
        String resource = String.valueOf(request.get("resource"));
        String fileType = resource.split("\\.")[resource.split("\\.").length - 1];
        return types.get(fileType);
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
    public void setBody() throws IOException {
        String resource = String.valueOf(request.get("resource"));
        Path path = Paths.get((root + resource));
        body = Files.readAllBytes(path);
    }

    @Override
    public void setHeader(String type) {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void run() {
        try {
            setBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String type;
        type = determineFileType();
        setHeader(type);
        setResponse(200);
    }

    @Override
    public void stop() throws InterruptedException {
        if (respond != null)
            respond.join();
    }
}