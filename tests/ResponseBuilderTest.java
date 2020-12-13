import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBuilderTest {

    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
    }

    @Test
    public void buildBlankResourceResponse() throws IOException, ExceptionInfo {
        RequestParser parser = new RequestParser();
        Responder responder = new FileResponder(new File(".").getCanonicalPath());
        String request = "GET HTTP/1.1";
        Request requestMap = parser.parse(request.getBytes());
        Response response = responder.respond(requestMap);
        ResponseBuilder builder = new ResponseBuilder(response);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write("HTTP 200 OK\r\n".getBytes());
        target.write("Content-Type: text/html\r\n".getBytes());
        target.write(("Content-Length: " + String.valueOf(helper.contentLength) + "\r\n").getBytes());
        target.write("\r\n".getBytes());
        target.write(helper.body);

        byte[] result = builder.getResponse();

        assertArrayEquals(target.toByteArray(), result);



    }
}
