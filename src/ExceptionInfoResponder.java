import com.sun.deploy.cache.BaseLocalApplicationProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoResponder implements Responder {
    Response response;
    Map<String, String> header;
    private byte[] body;
    private boolean bodyIsSet;
    private String serverName;
    private Request request;

    public ExceptionInfoResponder(String serverName) {
        this.serverName = serverName;
        response = new Response();
    }

    public Response respond(Request request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        setHeader();
        setResponse();
        return response;
    }

    @Override
    public void setHeader() throws IOException {
        if(!bodyIsSet)
            setBody();
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException {
        String message = request.get("message");
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
