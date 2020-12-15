import com.sun.deploy.cache.BaseLocalApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListingResponder implements Responder {
    private final String root;
    private final String serverName;
    Response response;
    Map<String, String> header;
    private byte[] body;
    private Request request;

    public ListingResponder(String serverName, String root) {
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
    public void setHeader() throws IOException {
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", "text/html");
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws IOException, ExceptionInfo {
        File directory;
        String p = "";
        if (request.get("resource").contains("img")) {
            directory = new File(root + "/img");
            p = "/img";
        } else
            directory = new File(root);
        File[] files = directory.listFiles();
        String bodyMsg = "<ul>";
        for (File file : files) {
            if (file.isDirectory()) {
                String pd = "/listing";
                bodyMsg = bodyMsg + "<li><a href=\"" + pd + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
            }
            else
                bodyMsg = bodyMsg + "<li><a href=\"" + p + "/" + file.getName() + "\">" + file.getName() + "</a></li>";
        }
        bodyMsg = bodyMsg + "</ul>";
        body = bodyMsg.getBytes();
    }

    @Override
    public void setResponse() {
        response.put("statusCode", 200);
        response.put("headers", header);
        response.put("body", body);
    }
}
