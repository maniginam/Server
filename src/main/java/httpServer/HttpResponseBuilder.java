package httpServer;

import server.ResponseBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponseBuilder implements ResponseBuilder {
    private ByteArrayOutputStream response;
    private Map<String, Object> responseMap;
    private String statusLine;
    private String headers;
    private byte[] body;
    private String responseMsg;

    public HttpResponseBuilder() {
        response = new ByteArrayOutputStream();
    }

    @Override
    public byte[] buildResponse(Map<String, Object> responseMap) throws IOException {
        response = new ByteArrayOutputStream();
        this.responseMap = responseMap;
        writeStatusLIne();
        writeHeaders();
        writeBody();
        response.write(responseMsg.getBytes());
        if (body != null)
            response.write(body);
        return response.toByteArray();
    }

    private void writeStatusLIne() {
//        System.out.println("HttpRESPONSEBUILDER responseMap = " + responseMap);
        int statusCode = (int) responseMap.get("statusCode");
        if (statusCode == 200)
            statusLine = "HTTP/1.1 " + statusCode + " OK\r\n";
        else if (statusCode == 404)
            statusLine = "HTTP/1.1 " + statusCode + " page not found\r\n";
        else if(statusCode == 301)
            statusLine = "HTTP/1.1" + statusCode + "redirect!";
        responseMsg = statusLine;
    }

    private void writeHeaders() {
        headers = "";
        for (String key : responseMap.keySet()) {
            if(key != "body" && key != "statusCode")
                responseMsg = responseMsg + key + ": " + responseMap.get(key) + "\r\n";
        }
        headers = headers + "\r\n";
        responseMsg = responseMsg + "\r\n";
    }

    private void writeBody() {
        body = (byte[]) responseMap.get("body");
    }

    public byte[] getResponse() {
        return response.toByteArray();
    }
}
