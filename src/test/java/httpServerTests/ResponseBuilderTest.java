package test.java.httpServerTests;

import main.java.httpServer.FileResponder;
import main.java.httpServer.HttpResponseBuilder;
import main.java.httpServer.RequestParser;
import main.java.server.ExceptionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBuilderTest {
    private HttpTestHelper helper;
    private RequestParser parser;
    private FileResponder responder;
    private Map<String, Object> requestMap;
    private Map<String, Object> response;
    private HttpResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        helper = new HttpTestHelper(1003);
        requestMap = new HashMap<String, Object>();
    }

    @Test
    public void buildBlankResourceResponse() throws IOException, ExceptionInfo, InterruptedException {
        String request = "GET HTTP/1.1\r\n\r\n";
        String root = helper.root;
        parser = helper.getParser(request);
        requestMap = parser.parse();
        responder = new FileResponder("Leo's Server", root);
        response = responder.respond(requestMap);

        helper.setResource("/index.html");
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write("HTTP/1.1 200 OK\r\n".getBytes());
        target.write("Server: Leo's Server\r\n".getBytes());
        target.write(("Content-Length: " + helper.getContentLength() + "\r\n").getBytes());
        target.write("Content-Type: text/html\r\n".getBytes());
        target.write("\r\n".getBytes());
        target.write(helper.getBody());

        responder = new FileResponder("Leo's Server", root);
        response = responder.respond(requestMap);
        builder = new HttpResponseBuilder();
        byte[] result = builder.buildResponse(response);

        assertEquals("HTTP/1.1 200 OK\r\n", builder.getStatusLine());
        assertEquals("Server: Leo's Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", builder.getHeaders());
        assertArrayEquals(helper.getBody(), builder.getBody());
        assertArrayEquals(target.toByteArray(), result);


    }
}
