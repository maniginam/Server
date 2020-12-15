import java.io.IOException;

public class ExceptionInfo extends Throwable {
    private final Request request;
    private final Response response;
    private String message;

    public ExceptionInfo (String message) throws IOException {
        super(message);
        this.setMessage(message);
        request = new Request();
        request.put("message", message);
        Responder responder = new ExceptionInfoResponder();
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
