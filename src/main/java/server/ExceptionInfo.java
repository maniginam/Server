package server;

import httpServer.ExceptionInfoResponder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfo extends Throwable {
    public static String serverName;
    private ExceptionInfoResponder responder;
    private final Map<String, Object> request;
    private final Map<String, Object> response;
    private String message;

    public ExceptionInfo(String message) throws IOException, ExceptionInfo {
        super(message);
        this.setMessage(message);
        request = new HashMap<String, Object>();
        request.put("message", message);
        responder = new ExceptionInfoResponder(serverName);
        response = responder.respond(request);
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) throws IOException {
        this.message = message;

    }

}
