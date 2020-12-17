import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileResponder implements Responder {
    private final String root;
    private final String serverName;
    Response response;
    Map<String, String> header;
    private byte[] body;
    public Request request;

    public FileResponder(String serverName, String root) {
        this.root = root;
        this.serverName = serverName;
        response = new Response();
    }

    public Response respond(Request request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        String type = "text/html";
        if (String.valueOf(request.get("resource")).contains(".pdf"))
            type = "application/pdf";
        setHeader(type);
        setResponse(200);
        return response;
    }

    @Override
    public void setResponse(int statusCode) {
        response.put("statusCode", statusCode);
        response.put("headers", header);
        response.put("body", body);
    }

    @Override
    public void setBody() throws ExceptionInfo, IOException {
        String resource = String.valueOf(request.get("resource"));
        Path path = Paths.get((root + resource));
        try {
            body = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO: 12/15/20 This feels ugly--ask about this
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        }
    }

    @Override
    public void setHeader(String type) {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }


}