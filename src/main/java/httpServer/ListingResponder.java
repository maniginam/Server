package httpServer;

import server.Responder;
import server.ResponseBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListingResponder implements Responder {
    private final String root;
    Map<String, Object> responseMap;
    Map<String, String> header;
    private byte[] body;
    private Map<String, Object> request;
    private byte[] response;

    public ListingResponder(String serverName, String root) {
        this.root = root;
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "text/html");
    }

    @Override
    public byte[] respond(Map<String, Object> request, ResponseBuilder builder) throws IOException {
        this.request = request;
        responseMap.put("body", getListings());
        responseMap.put("Content-Length", String.valueOf(body.length));
        response = builder.buildResponse(responseMap);
        return response;
    }

    public byte[] getListings() {
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
        return body;
    }
}