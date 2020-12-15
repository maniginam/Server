import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResponseBuilder {
    private ByteArrayOutputStream response;
    private Response responseMap;
    private String statusLine;
    private String headers;
    private byte[] body;

    public ResponseBuilder() {
        response = new ByteArrayOutputStream();
    }

    public byte[] buildResponse(Response responseMap) throws IOException {
        this.responseMap = responseMap;
        writeStatusLIne();
        writeHeaders();
        writeBody();
        return response.toByteArray();
    }

    private void writeStatusLIne() throws IOException {
        int statusCode = (int) responseMap.get("statusCode");
        if (statusCode == 200)
            statusLine = "HTTP/1.1 " + statusCode + " OK\r\n";
        else if (statusCode == 404)
            statusLine = "HTTP/1.1 " + statusCode + " page not found\r\n";
        response.write(statusLine.getBytes());
    }

    private void writeHeaders() throws IOException {
        headers = "";
        Map<String, String> headerMap = (Map<String, String>) responseMap.get("headers");
        Set<String> headerKeys = headerMap.keySet();
        for (String header : headerKeys) {
            headers = headers + header + ": " + headerMap.get(header) + "\r\n";
        }
        headers = headers + "\r\n";
        response.write(headers.getBytes());
    }

    private void writeBody() throws IOException {
        body = (byte[]) responseMap.get("body");
        response.write(body);
        response.toByteArray();
    }

    public byte[] getResponse() {
        return response.toByteArray();
    }

    public String getStatusLine() {
        return statusLine;
    }

    public String getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
