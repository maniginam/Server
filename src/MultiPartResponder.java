import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiPartResponder implements Responder {
    private final String serverName;
    private final Response response;
    private Request request;
    private Map<String, String> header;
    private byte[] body;
    private String fileName;
    private String type;

    public MultiPartResponder(String serverName) {
        this.serverName = serverName;
        response = new Response();
    }

    @Override
    public Response respond(Request request) throws IOException, ExceptionInfo {
        this.request = request;
        type = "application/octet-stream";
        System.out.println("MULTIPARTRESPONDER request = " + request);
        setBody();
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
    public void setHeader(String type) throws IOException, ExceptionInfo {
        if (body == null) {
            setBody();
        }
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException {
        String bodyMsg = "<h2>POST Form</h2>" +
                "<li>file name: " + String.valueOf(request.get("fileName")).replace("\"", "") + "</li>" +
                "<li>file size: " + request.get("fileSize") + "</li>" +
                "<li>content type: " + type + "</li>";
        System.out.println("bodyMsg = " + bodyMsg);
        body = bodyMsg.getBytes();
    }
}
