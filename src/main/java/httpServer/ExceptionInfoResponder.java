package httpServer;

import server.ExceptionInfo;
import server.Responder;
import server.ResponseBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Map<String, Object> responseMap;
    Map<String, String> header;
    private byte[] body;
    private boolean bodyIsSet;
    private String serverName;
    private Map<String, Object> request;
    private byte[] response;

    public ExceptionInfoResponder(String serverName) {
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        responseMap.put("statusCode", 404);
        responseMap.put("Content-Type", "text/html");
    }

    public byte[] respond(Map request, ResponseBuilder builder) throws IOException, ExceptionInfo {
        this.request = request;
        responseMap.put("body", makeMessage());
        responseMap.put("Content-Length", String.valueOf(body.length));
        response = builder.buildResponse(responseMap);
        return response;
    }

    public byte[] makeMessage() {
        String message = String.valueOf(request.get("message"));
        bodyIsSet = true;
        body = ("<h1>" + message + "</h1>").getBytes();
        return body;
    }
}