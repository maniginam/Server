import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BasicRequestsTest {
    private TestHelper helper;
    private TestConnectionFactory connectionFactory;
    private SocketHost host;
    private HttpConnection connection;
    private Socket socket;
    private RequestParser parser;
    private List<Connection> connections;
    private Router router;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        connectionFactory = new TestConnectionFactory(3141, String.valueOf(new File(".").getCanonicalPath()));
        router = new Router();
        host = new SocketHost(3141, connectionFactory, router);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (socket != null)
            socket.close();
    }

    @Test
    public void submitBlankTargetRequest() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        OutputStream output = helper.getOutput();
        BufferedInputStream input = helper.getBuffedInput();

        Map<String, String> target = new HashMap<String, String>();
        target.put("method", "GET");
        target.put("resource", "/index.html");
        target.put("httpVersion", "HTTP/1.1");

        String request = "GET HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());

        connections = host.getConnections();

        for (Connection connection : connections) {
            connection.getResponseBuilder().buildResponse();
            byte[] result = connection.getResponseBuilder().getResponse();
            String responseStatus = connection.getResponseBuilder().getStatus();
            String responseHeader = connection.getResponseBuilder().getHeaders();
            assertEquals("HTTP/1.1 200 OK\r\n", responseStatus);
            assertEquals("Content-Length: " + helper.contentLength + "\r\n" +
                    "Content-Type: text/html\r\n\r\n", responseHeader);
            assertArrayEquals(result, result);
        }

    }

//    @Test
//    public void submitSlashTargetRequest() throws IOException, ExceptionInfo {
//        String request = "GET / HTTP/1.1";
//        Map<String, String> target = new HashMap<String, String>();
//        target.put("method", "GET");
//        target.put("target", "/index.html");
//        target.put("httpVersion", "HTTP/1.1");
//
//        Map<String, String> result = connection.getRequestParser().parse(request);
//
//        assertEquals(target, result);
//    }
//
//    @Test
//    public void submitIndexTargetRequest() throws IOException, ExceptionInfo {
//        String request = "GET /index.html HTTP/1.1";
//        Map<String, String> target = new HashMap<String, String>();
//        target.put("method", "GET");
//        target.put("target", "/index.html");
//        target.put("httpVersion", "HTTP/1.1");
//
//        Map<String, String> result = connection.getRequestParser().parse(request);
//
//        assertEquals(target, result);
//    }
//
//    @Test
//    public void garbageMethod() throws IOException, ExceptionInfo {
//        String request = "Rex /index.html HTTP/1.1";
//
//        assertThrows(ExceptionInfo.class, () -> {
//            connection.getRequestParser().parse(request);
//        });
//    }
//
//
//    @Test
//    public void respondToBlankTargetRequest() throws IOException {
//        host.start();
//        connect();
//        String request = "GET HTTP/1.1";
//
//
//    }
}

