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
        String request = "GET HTTP/1.1\r\n\r\n";

        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request.getBytes());

        assertEquals(target, result);
        assertEquals("GET", result.get("method"));
        assertEquals("/index.html", result.get("resource"));
        assertEquals("HTTP/1.1", result.get("httpVersion"));
    }

    @Test
    public void parseSlashTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET / HTTP/1.1\r\n\r\n";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request.getBytes());
        assertEquals(target, result);
    }

    @Test
    public void parseIndexTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET /index.html HTTP/1.1\r\n\r\n";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        Request result = parser.parse(request.getBytes());
        assertEquals(target, result);
    }

    @Test
    public void garbageMethod() throws IOException, ExceptionInfo {
        String request = "Rex /index.html HTTP/1.1\r\n\r\n";

        assertThrows(ExceptionInfo.class, () -> {
            parser.parse(request.getBytes());
        });
    }
}