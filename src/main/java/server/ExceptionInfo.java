package main.java.server;

import main.java.httpServer.ExceptionInfoResponder;

import java.io.IOException;

public class ExceptionInfo extends Throwable {
    public static String serverName;
    private ExceptionInfoResponder responder;
    private final Request request;
    private final Response response;
    private String message;

    public ExceptionInfo(String message) throws IOException, ExceptionInfo {
        super(message);
        this.setMessage(message);
        request = new Request();
        request.put("message", message);
        responder = new ExceptionInfoResponder(serverName);
        response = responder.respond(request);
    }

    public Response getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) throws IOException {
        this.message = message;

    }

}
