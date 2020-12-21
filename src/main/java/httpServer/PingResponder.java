package httpServer;

import server.Responder;
import server.ResponseBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PingResponder implements Responder {
    private final String serverName;
    private final Map<String, Object> responseMap;
    private Map<String, Object> request;
    private byte[] body;
    private Map<String, String> header;
    private byte[] response;

    public PingResponder(String serverName) {
        this.serverName = serverName;
        responseMap = new HashMap<>();
        responseMap.put("Server", serverName);
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "text/html");
    }

    @Override
    public byte[] respond(Map<String, Object> request, ResponseBuilder builder) throws IOException {
        this.request = request;
        responseMap.put("body", makeMessage());
        responseMap.put("Content-Length", String.valueOf(body.length));
        response = builder.buildResponse(responseMap);
        return response;
    }

    public byte[] makeMessage() {
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
        return body;
    }

    private int getWait() {
        String[] pingArgs = String.valueOf(request.get("resource")).split("/");
        if (pingArgs.length > 2)
            return Integer.parseInt(pingArgs[2]);
        else return 0;
    }
}
