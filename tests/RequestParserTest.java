import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestParserTest {
    private RequestParser parser;

    @BeforeEach
    public void setup() throws IOException {
        parser = new RequestParser();
    }

    @Test
    public void parseBlankTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET HTTP/1.1";

        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request);
        String method = parser.getMethod();
        String resource = parser.getResource();

        assertEquals("GET", method);
        assertEquals("/index.html", resource);
        assertEquals(target, result);
    }

    @Test
    public void parseSlashTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET / HTTP/1.1";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request);
        String method = parser.getMethod();
        String resource = parser.getResource();

        assertEquals("GET", method);
        assertEquals("/index.html", resource);
        assertEquals(target, result);
    }

    @Test
    public void parseIndexTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET /index.html HTTP/1.1";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request);
        String method = parser.getMethod();
        String resource = parser.getResource();

        assertEquals("GET", method);
        assertEquals("/index.html", resource);
        assertEquals(target, result);
    }

    @Test
    public void garbageMethod() throws IOException, ExceptionInfo {
        String request = "Rex /index.html HTTP/1.1";

        assertThrows(ExceptionInfo.class, () -> {
            parser.parse(request);
        });
    }
}
