import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
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
        String root = helper.root;
        requestMap = parser.parse(request.getBytes());
        responder = new FileResponder(root);
        response = responder.respond(requestMap);

        helper.setResource("/index.html");
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write("HTTP/1.1 200 OK\r\n".getBytes());
        target.write(("Content-Length: " + helper.getContentLength() + "\r\n").getBytes());
        target.write("Content-Type: text/html\r\n".getBytes());
        target.write("\r\n".getBytes());
        target.write(helper.getBody());

        responder = new FileResponder(root);
        response = responder.respond(requestMap);
        builder = new ResponseBuilder();
        byte[] result = builder.buildResponse(response);

        assertEquals("HTTP/1.1 200 OK\r\n", builder.getStatusLine());
        assertEquals("Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", builder.getHeaders());
        assertArrayEquals(helper.getBody(), builder.getBody());
        assertArrayEquals(target.toByteArray(), result);


    }
}
