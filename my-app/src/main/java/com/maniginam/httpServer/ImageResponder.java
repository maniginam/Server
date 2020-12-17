package httpServer;

import server.ExceptionInfo;
import server.Request;
import server.Responder;
import server.Response;

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
        String resource = String.valueOf(request.get("resource"));
        String fileType = resource.split("\\.")[resource.split("\\.").length - 1];
        if (fileType.contains("jpg"))
            fileType = "jpeg";
        setHeader("image/" + fileType);
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
    public void setHeader(String type) {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }
}