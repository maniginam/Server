package httpServer;

import server.Responder;
import server.ResponseBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormResponder implements Responder {
    private final Map<String, Object> responseMap;
    private Map<String, Object> request;
    private byte[] body;
    private byte[] response;

    public FormResponder(String serverName) {
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "text/html");
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
        String[] entries = String.valueOf(request.get("resource")).split("[?=&]");
        String bodyMsg = "<h2>GET Form</h2>";
        for (int i = 1; i < entries.length; i= i+2) {
            bodyMsg = bodyMsg + "<li>" +
                    entries[i] + ": " + entries[i+1] +
                    "</li>";
        }
        body = bodyMsg.getBytes();
        return body;
    }
}
