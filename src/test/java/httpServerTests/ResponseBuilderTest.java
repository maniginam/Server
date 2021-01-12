package httpServerTests;

import httpServer.HttpResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseBuilderTest {
    private HttpTestHelper helper;
    private HttpResponseBuilder builder;
    private Map<String, Object> responseMap;

    @BeforeEach
    public void setup() throws IOException {
        helper = new HttpTestHelper(1003);
    }

    @Test
    public void buildBlankResourceResponse() throws IOException {
        builder = new HttpResponseBuilder();
        responseMap = new HashMap<>();

        helper.setResource("/index.html");
        responseMap.put("statusCode", 200);
        responseMap.put("Server", "Leo's Server");
        responseMap.put("Content-Length", +helper.getContentLength());
        responseMap.put("Content-Type", "text/html");
        responseMap.put("body", helper.getBody());

        byte[] result = builder.buildResponse(responseMap);
        String message = helper.readResponseBodyResult(result);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write(("HTTP/1.1 200 OK\r\n" +
                "Server: Leo's Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n").getBytes());
        target.write(helper.getBody());

        assertTrue(message.contains("HTTP/1.1 200 OK"));
        assertTrue(message.contains("Server: Leo's Server"));
        assertTrue(message.contains("Content-Length: " + helper.getContentLength()));
        assertTrue(message.contains("Content-Type: text/html"));
        assertArrayEquals(target.toByteArray(), result);
    }
}
