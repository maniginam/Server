import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Response response;
    Map<String, String> header;
    private byte[] body;
    private boolean bodyIsSet;

    public ExceptionInfoResponder() {
        response = new Response();
    }

    @Override
    public Response respond(Request request) throws IOException {
        bodyIsSet = false;
        setBody(request.get("message"));
        setHeader();
        setResponse();
        return response;
    }

    @Override
    public void setHeader() throws IOException {
        if(!bodyIsSet)
            setBody(request.get("message"));
        header = new HashMap<>();
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody(String message) throws IOException {
        bodyIsSet = true;
        body = ("<h1>" + message + "</h1>").getBytes();
    }

    @Override
    public void setResponse() {
        response.put("statusCode", 404);
        response.put("headers", header);
        response.put("body", body);
    }
}
