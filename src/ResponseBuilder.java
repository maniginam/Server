import com.sun.deploy.cache.CacheEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResponseBuilder {
    private final Response responseMap;
    private ByteArrayOutputStream response;

    public ResponseBuilder(Response responseMap) throws IOException {
        this.responseMap = responseMap;
        response = new ByteArrayOutputStream();

        writeStatusLIne();
        writeHeaders();
        writeBody();
    }

    private void writeStatusLIne() throws IOException {
        response.write(("HTTP/1.1" + responseMap.get("status") + "OK\r\n").getBytes());
    }

    private void writeHeaders() throws IOException {
        Set<String> headerKeys = ((Map<String, String>) responseMap.get("headers")).keySet();
        for (String header : headerKeys) {
            response.write((header + "\r\n").getBytes());
        }
        response.write("\r\n".getBytes());
    }

    private void writeBody() throws IOException {
        response.write((byte[]) responseMap.get("body"));
    }

    public byte[] getResponse() {
        return response.toByteArray();
    }
}
