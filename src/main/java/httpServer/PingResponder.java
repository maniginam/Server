package main.java.httpServer;

import main.java.server.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PingResponder implements Responder {
    private final String serverName;
    private final Map<String, Object> response;
    private Map<String, Object> request;
    private byte[] body;
    private Map<String, String> header;

    public PingResponder(String serverName) {
        this.serverName = serverName;
        response = new HashMap<>();
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo, InterruptedException {
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
    public void setHeader(String type) throws IOException, ExceptionInfo, InterruptedException {
        if (body == null) {
            setBody();
        }
        header = new HashMap<>();
        header.put("Server", serverName);
        header.put("Content-Type", type);
        header.put("Content-Length", String.valueOf(body.length));
    }

    @Override
    public void setBody() throws InterruptedException {
        int wait = getWait();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        Thread.sleep(wait * 1000);
        Date later = new Date();
        int sleep = Math.decrementExact((int)later.getTime() - (int)now.getTime()) / 1000;
        System.out.println("sleep = " + sleep);

        body = ("<html>\r\n" +
                "<h2>Ping</h2>\r\n" +
                "<li>start time: " + formatter.format(now) + "</li>\r\n" +
                "<li>end time: " + formatter.format(later) + "</li>\r\n" +
                "<li>sleep seconds: " + sleep + "</li>\r\n" +
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
