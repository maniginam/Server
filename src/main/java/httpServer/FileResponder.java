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
    // COMPLETE TODO: 1/12/21 Types should be static. Don't create the map in constructor for every file responder.
    private byte[] body;
    public Map<String, Object> request;
    Map<String, Object> responseMap;
    private final Map<String, String> types = new HashMap<String, String>() {{
        put("html", "text/html");
        put("pdf", "application/pdf");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
    }};

    public FileResponder(String root) {
        this.root = root;
        responseMap = new HashMap<>();

    }

    public Map<String, Object> respond(Map<String, Object> request) throws IOException {
//        System.out.println("FILE RESPONDER request = " + request);
        this.request = request;
        request.put("clear-cookies", "*");
        String type = determineFileType();
        responseMap.put("body", readFile());
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", type);
        responseMap.put("Content-Length", body.length);
        responseMap.put("Set-Cookie", String.valueOf(request.get("cookie")));
        if (request.containsKey("clear-cookies"))
            responseMap.put("Clear-Site-Data: ",  "*");


        return responseMap;
    }

    private String determineFileType() {
        String resource = String.valueOf(request.get("resource"));
        final String[] split = resource.split("\\.");
        String fileType = split[split.length - 1];
        return types.get(fileType);
    }

    public byte[] readFile() {
        String resource = String.valueOf(request.get("resource"));
        if (resource.contains("favicon.ico"))
            resource = "/favicon.ico";
        Path path = Paths.get((root + resource));
        try {
            body = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}