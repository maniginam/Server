package httpServer;

import server.Responder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Map<String, Object> responseMap;
    private byte[] body;
    private Map<String, Object> request;

    public ExceptionInfoResponder() {
        responseMap = new HashMap<>();
        responseMap.put("statusCode", 404);
        responseMap.put("Content-Type", "text/html");
    }

    public Map<String, Object> respond(Map request) throws IOException {
        this.request = request;
        responseMap.put("body", makeMessage());
        responseMap.put("Content-Length", String.valueOf(body.length));
        return responseMap;
    }

    public byte[] makeMessage() {
        String message = String.valueOf(request.get("message"));
        body = ("<h1>" + message + "</h1>").getBytes();
        return body;
    }
}