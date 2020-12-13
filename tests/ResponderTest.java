import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponderTest {

    private RequestParser parser;
    private Request requestMap;
    private Response response;
    private Responder responder;
    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        parser = new RequestParser();
        requestMap = new Request();
        response = new Response();
    }

    @Test
    public void responseToBlankGET () throws IOException, ExceptionInfo {
        String request = "GET HTTP/1.1\r\n\r\n";
        String root = helper.pathName;
        requestMap = parser.parse(request.getBytes());
        responder = new FileResponder(root);
        response = responder.respond(requestMap);
        byte[] body = helper.body;
        int contentLength = helper.contentLength;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Length", String.valueOf(contentLength));
        headers.put("Content-Type", "text/html");
        Map<String, Object> target = new HashMap<String, Object>();
        target.put("status", "HTTP/1.1 200 OK");
        target.put("headers", headers);
        target.put("body", body);

        assertEquals(200, response.get("status"));
        assertEquals(headers, response.get("headers"));
        assertArrayEquals(body, (byte[]) response.get("body"));
    }

}
