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
    public void setBody() throws ExceptionInfo, IOException {
        String resource = request.get("resource");
        Path path = Paths.get((root + resource));
        try {
            body = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO: 12/15/20 This feels ugly--ask about this
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        }
    }

    @Override
    public void setHeader() {
        header = new HashMap<>();
        header.put("Server", serverName);
        // TODO: 12/15/20 FIX THIS! 
        if (request.get("resource").contains(".html"))
            header.put("Content-Type", "text/html");
        else if (request.get("resource").contains(".pdf"))
            header.put("Content-Type", "application/pdf");
        header.put("Content-Length", String.valueOf(body.length));
    }


}
