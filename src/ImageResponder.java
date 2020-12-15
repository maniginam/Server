import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ImageResponder implements Responder {
    private final String root;
    private final String serverName;
    Response response;
    Map<String, String> header;
    private byte[] body;
    public Request request;

    public ImageResponder(String serverName, String root) {
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
            File file = new File(root + resource);
            FileInputStream input = new FileInputStream(file);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] inputBytes = Files.readAllBytes(path);
            output.write(inputBytes);
            body = Files.readAllBytes(path);

//            body = output.toByteArray();

        } catch (IOException e) {
            // TODO: 12/15/20 This feels ugly--ask about this
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        }
    }

    @Override
    public void setHeader() {
        String fileType = request.get("resource").split("\\.")[request.get("resource").split("\\.").length - 1];
        if (fileType.contains("jpg"))
            fileType = "jpeg";
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", "image/" + fileType);
        header.put("Content-Length", String.valueOf(body.length));
    }


}
