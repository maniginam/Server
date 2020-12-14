import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GarbageResponder implements Responder {
    Response response;
    Map<String, String> header;
    private byte[] body;

    public GarbageResponder() {
        response = new Response();
    }

    @Override
    public Response respond(Request request) throws IOException {
        setBody(request.get("resource"));
        setHeader();
        setResponse();
        return response;
    }

    @Override
    public void setHeader() {
        header = new HashMap<>();
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody(String message) throws IOException {
        body = ("<h1>" + message + "</h1>\r\n").getBytes();
    }

    @Override
    public void setResponse() {
        response.put("status", 404);
        response.put("headers", header);
        response.put("body", body);
    }
}
