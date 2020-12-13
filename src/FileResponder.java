import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResponder implements Responder {
    private final String root;
    private int contentLength;
    private String resource;
    private byte[] body;

    public FileResponder(String root) {
        this.root = root;
    }

    @Override
    public Response respond(Request request) throws IOException {
        resource = request.get("resource");
        setBody();
        setHeader();
        setResponse();
        return response;
    }

    private void setResponse() {
        response.put("status", 200);
        response.put("headers", header);
        response.put("body", body);
    }

    @Override
    public void setBody() throws IOException {
        Path path = Paths.get(root);
        body = Files.readAllBytes(path);
    }

    @Override
    public void setHeader() throws IOException {
        header.put("Content-Type", "text/html");
        header.put("Content-Length", String.valueOf(body.length));
    }


}
