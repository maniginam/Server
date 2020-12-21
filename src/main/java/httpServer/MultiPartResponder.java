package httpServer;

import server.Responder;
import server.ResponseBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiPartResponder implements Responder {
    private final Map<String, Object> responseMap;
    private Map<String, Object> request;
    private byte[] body;
    private String type;
    private byte[] response;

    public MultiPartResponder(String serverName) {
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        responseMap.put("statusCode", 200);
        type = "application/octet-stream";
        responseMap.put("Content-Type", type);
    }

    @Override
    public byte[] respond(Map<String, Object> request, ResponseBuilder builder) throws IOException {
        this.request = request;
        responseMap.put("body", makeMessage());
        responseMap.put("Content-Length", String.valueOf(body.length));
        response = builder.buildResponse(responseMap);
        return response;
    }

    public byte[] makeMessage() {
        String bodyMsg = "<h2>POST Form</h2>" +
                "<li>file name: " + String.valueOf(request.get("fileName")).replace("\"", "") + "</li>" +
                "<li>file size: " + request.get("fileSize") + "</li>" +
                "<li>content type: " + type + "</li>";
        body = bodyMsg.getBytes();
        return body;
    }
}
