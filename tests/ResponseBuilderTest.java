import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBuilderTest {

    private TestHelper helper;
    private RequestParser parser;
    private FileResponder responder;
    private Request requestMap;
    private Response response;
    private ResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        parser = new RequestParser();
        requestMap = new Request();
        response = new Response();
    }

    @Test
    public void buildBlankResourceResponse() throws IOException, ExceptionInfo {
        String request = "GET HTTP/1.1\r\n\r\n";
        String root = helper.pathName;
        requestMap = parser.parse(request.getBytes());
        responder = new FileResponder(root);
        response = responder.respond(requestMap);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write("HTTP 200 OK".getBytes());
        target.write("Content-Type: text/html".getBytes());
        target.write(("Content-Length: " + String.valueOf(helper.contentLength)).getBytes());
        target.write(helper.body);

        responder = new FileResponder(root);
        response = responder.respond(requestMap);
        builder = new ResponseBuilder(response);
        byte[] result = builder.buildResponse();

        assertEquals("HTTP/1.1 200 OK\r\n", builder.getStatus());
        assertEquals("Content-Length: " + String.valueOf(helper.contentLength) + "\r\n" +
                "Content-Type: text/html\r\n", builder.getHeaders());
        assertArrayEquals(helper.body, builder.getBody());
//        assertArrayEquals(target.toByteArray(), result);


    }
}
