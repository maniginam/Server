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

    @BeforeEach
    public void setup() {
        parser = new RequestParser();
        requestMap = new Request();
        response = new Response();
    }

    @Test
    public void responseToBlankGET () throws IOException, ExceptionInfo {
        String request = "GET HTTP/1.1\r\n\r\n";
        String pathName = new File(".").getCanonicalPath() + "/testroot/index.html";
        requestMap = parser.parse(request.getBytes());
        responder = new FileResponder(pathName);
        response = responder.respond(requestMap);

        Path path = Paths.get(pathName);
        File file = new File(String.valueOf(path));
        byte[] body = Files.readAllBytes(path);
        int contentLength = body.length;

        ByteArrayInputStream inputArray = new ByteArrayInputStream(body);
        // TODO: 12/12/20 HAD TO PUT THIS INTO A BYTEARRAYINPUTSTREAM IN THE LAST ONE

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
