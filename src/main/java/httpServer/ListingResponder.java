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
        String parent = "";
        String child = "";
        String target = String.valueOf(request.get("resource"));
        String[] subTargets = target.split("/");
        int targets = subTargets.length;
        if (targets < 3) {
            parent = "/" + subTargets[1];
            directory = new File(root);
        } else {
            for (int i = 2; i < targets; i++) {
                child = "/" + child + subTargets[i];
            }
            directory = new File(root + "/" + child);
        }

        File[] files = directory.listFiles();
        String bodyMsg = "<ul>";
        for (File file : files) {
            if (file.isDirectory()) {
                bodyMsg = bodyMsg + "<li><a href=\"" + parent + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
            }
            else
                bodyMsg = bodyMsg + "<li><a href=\"" + child + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
        }
        bodyMsg = bodyMsg + "</ul>";
        body = bodyMsg.getBytes();
        return body;
    }
}
