package httpServer;

import server.Responder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListingResponder implements Responder {
    private final String root;
    Map<String, Object> responseMap;
    private byte[] body;
    private Map<String, Object> request;

    public ListingResponder(String root) {
        this.root = root;
        responseMap = new HashMap<>();
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "text/html");
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) throws IOException {
        this.request = request;
        responseMap.put("body", getListings());
        responseMap.put("Content-Length", String.valueOf(body.length));
        return responseMap;
    }

    public byte[] getListings() {
        File directory;
        String parent = "";
        String child = "";
        String resource = String.valueOf(request.get("resource"));
        String[] members = resource.split("/");
        int memberCount = members.length;
        if (memberCount < 3) {
            parent = "/" + members[1];
            directory = new File(root);
        } else {
            for (int i = 2; i < memberCount; i++) {
                child = "/" + child + members[i];
            }
            directory = new File(root + "/" + child);
        }
        return writeListings(directory, parent, child);
    }

    private byte[] writeListings(File directory, String parent, String child) {
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
