package httpServerTests;

import httpServer.RequestParser;
import server.ExceptionInfo;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestParserTest {

    @Test
    public void parseBlankTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET HTTP/1.1\r\n\r\n";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        RequestParser parser = new RequestParser(buffedInput);
        Map<String, Object> result = parser.parse();

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

        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        RequestParser parser = new RequestParser(buffedInput);
        Map<String, Object> result = parser.parse();
        assertEquals(target, result);
    }

    @Test
    public void parseIndexTargetRequest() throws IOException, ExceptionInfo {
        String request = "GET /index.html HTTP/1.1\r\n\r\n";
        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        RequestParser parser = new RequestParser(buffedInput);
        Map<String, Object> result = parser.parse();
        assertEquals(target, result);
    }

    @Test
    public void garbageMethod() throws IOException, ExceptionInfo {
        String request = "Rex /index.html HTTP/1.1\r\n\r\n";

        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        RequestParser parser = new RequestParser(buffedInput);
        assertThrows(ExceptionInfo.class, () -> {
            parser.parse();
        });
    }
}
