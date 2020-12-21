package httpServer;

import server.ExceptionInfo;
import server.Responder;
import server.ResponseBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileResponder implements Responder {
    private final String root;
    private final Map<String, String> types;
    Map<String, Object> responseMap;
    private byte[] body;
    public Map<String, Object> request;
    private String type;
    private byte[] response;

    public FileResponder(String serverName, String root) {
        this.root = root;
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        types = new HashMap<>();
        types.put("html", "text/html");
        types.put("pdf", "application/pdf");
        types.put("jpg", "image/jpeg");
        types.put("jpeg", "image/jpeg");
        types.put("png", "image/png");
    }

    public byte[] respond(Map<String, Object> request, ResponseBuilder builder) throws IOException, ExceptionInfo {
        this.request = request;
        type = determineFileType();
        responseMap.put("body", readFile());
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", type);
        responseMap.put("Content-Length", String.valueOf(body.length));

        response = builder.buildResponse(responseMap);
        return response;
    }

    private String determineFileType() {
        String resource = String.valueOf(request.get("resource"));
        String fileType = resource.split("\\.")[resource.split("\\.").length - 1];
        return types.get(fileType);
    }

    public byte[] readFile() throws ExceptionInfo, IOException {
        String resource = String.valueOf(request.get("resource"));
        Path path = Paths.get((root + resource));
        try {
            body = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO: 12/15/20 This feels ugly--ask about this
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        }
        return body;
    }
}