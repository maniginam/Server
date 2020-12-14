import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasicRequestsTest {
    private TestHelper helper;
    private TestConnectionFactory connectionFactory;
    private Router router;
    private SocketHost host;
    private OutputStream output;
    private BufferedInputStream buffed;
    private Connection connection;
    private ResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        connectionFactory = new TestConnectionFactory(3141, helper.root);
        router = new Router();
        Server.registerResponders(router, helper.root);
        host = new SocketHost(3141, connectionFactory, router);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (helper.getSocket() != null)
            helper.getSocket().close();
    }

    private String readResponseBodyResult(byte[] body) throws IOException {
        InputStream bodyStream = new ByteArrayInputStream(body);
        InputStreamReader inputReader = new InputStreamReader(bodyStream);
        BufferedReader reader = new BufferedReader(inputReader);
        return reader.readLine();
    }

    private ResponseBuilder connectionBuilder(Connection connection) {
        return connection.getResponseBuilder();
    }

    @Test
    public void submitBlankTargetRequest() throws IOException, ExceptionInfo, InterruptedException {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");

        String request = "GET HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = connectionBuilder(connection);
        byte[] result = builder.getResponse();
        String responseStatus = builder.getStatus();
        String responseHeader = builder.getHeaders();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());

        assertEquals("HTTP/1.1 200 OK\r\n", responseStatus);
        assertEquals("Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", responseHeader);
        assertArrayEquals(result, result);
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);


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

