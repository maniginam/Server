import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileResponder implements Responder {
    private final String root;
    Response response;
    Map<String, String> header;
    private byte[] body;

    public FileResponder(String root) {
        this.root = root;
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
    public void setResponse() {
        response.put("statusCode", 200);
        response.put("headers", header);
        response.put("body", body);
    }

    @Override
    public void setBody(String resource) throws IOException {
        Path path = Paths.get((root + resource));
        body = Files.readAllBytes(path);
    }

    @Override
    public void setHeader() {
        header = new HashMap<>();
        header.put("Content-Type", "text/html");
        header.put("Content-Length", String.valueOf(body.length));
    }


}
