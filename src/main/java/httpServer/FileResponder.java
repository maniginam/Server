package main.java.httpServer;

import main.java.server.ExceptionInfo;
import main.java.server.Responder;

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

    public FileResponder(String serverName, String root) {
        this.root = root;
        this.serverName = serverName;
        response = new HashMap<String, Object>();
        types = new HashMap<>();
        types.put("html", "text/html");
        types.put("pdf", "application/pdf");
        types.put("jpg", "image/jpeg");
        types.put("jpeg", "image/jpeg");
        types.put("png", "image/png");
    }

    public Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        String type;
        type = determineFileType();
        setHeader(type);
        setResponse(200);
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
    }

    @Override
    public void setBody() throws ExceptionInfo, IOException {
        String resource = String.valueOf(request.get("resource"));
        Path path = Paths.get((root + resource));
        try {
            body = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO: 12/15/20 This feels ugly--ask about this
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        }
    }

    @Override
    public void setHeader(String type) {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }
}