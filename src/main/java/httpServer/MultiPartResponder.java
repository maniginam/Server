package httpServer;

import server.Responder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiPartResponder implements Responder {
    private final Map<String, Object> responseMap;
    private Map<String, Object> request;
    private byte[] body;

    public MultiPartResponder() {
        responseMap = new HashMap<>();
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "application/octet-stream");
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) throws IOException {
        this.request = request;
        responseMap.put("body", makePostMessage());
        responseMap.put("Content-Length", String.valueOf(body.length));
        return responseMap;
    }

    public byte[] makePostMessage() {
        String bodyMsg = "<h2>POST Form</h2>" +
                "<li>file name: " + String.valueOf(request.get("fileName")).replace("\"", "") + "</li>" +
                "<li>file size: " + request.get("fileSize") + "</li>" +
                "<li>content type: " + request.get("Content-Type") + "</li>";
        body = bodyMsg.getBytes();
        return body;
    }
}
