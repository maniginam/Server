import com.sun.deploy.cache.CacheEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResponseBuilder {
    private final Response responseMap;
    private ByteArrayOutputStream response;
    private String status;
    private String headers;
    private byte[] body;

    public ResponseBuilder(Response responseMap) {
        this.responseMap = responseMap;
        response = new ByteArrayOutputStream();
    }

    public byte[] buildResponse() throws IOException {
        writeStatusLIne();
        writeHeaders();
        writeBody();
        return response.toByteArray();
    }

    private void writeStatusLIne() throws IOException {
        status = "HTTP/1.1 " + responseMap.get("status") + " OK\r\n";
        response.write(status.getBytes());
    }

    private void writeHeaders() throws IOException {
        headers = "";
        Map<String, String> headerMap = (Map<String, String>) responseMap.get("headers");
        Set<String> headerKeys = headerMap.keySet();
        for (String header : headerKeys) {
            headers = headers + header + ": " + headerMap.get(header) + "\r\n";
            response.write((header + "\r\n").getBytes());
        }
        response.write("\r\n".getBytes());
    }

    private void writeBody() throws IOException {
        body = (byte[]) responseMap.get("body");
        response.write(body);
    }

    public byte[] getResponse() {
        return response.toByteArray();
    }

    public String getStatus() {
        return status;
    }

    public String getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
