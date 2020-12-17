import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PingResponder implements Responder {
    private final String serverName;
    private final Response response;
    private Request request;
    private byte[] body;
    private Map<String, String> header;

    public PingResponder(String serverName) {
        this.serverName = serverName;
        response = new Response();
    }

    @Override
    public Response respond(Request request) throws IOException, ExceptionInfo {
        this.request = request;
        setBody();
        setHeader("text/html");
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
    public void setBody() throws IOException, ExceptionInfo {
        int wait = getWait();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(new Date());
        String later = formatter.format(new Date(System.currentTimeMillis() + wait * 1000));

        body = ("<html>\r\n" +
                "<h2>Ping</h2>\r\n" +
                "<li>start time: " + now + "</li>\r\n" +
                "<li>end time: " + later + "</li>\r\n" +
                "<li>sleep seconds: " + wait + "</li>\r\n" +
                "</html>\r\n" +
                "\r\n").getBytes();
    }

    private int getWait() {
        String[] pingArgs = String.valueOf(request.get("resource")).split("/");
        if (pingArgs.length > 2)
            return Integer.parseInt(pingArgs[2]);
        else return 0;
    }
}
