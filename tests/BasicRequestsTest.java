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

    private ByteArrayOutputStream getFullTargetOutputArray(String responseStatus, String responseHeader) throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write((responseStatus + responseHeader).getBytes());
        target.write(builder.getBody());
        return target;
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
        String responseStatus = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray(responseStatus, responseHeader);

        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", responseStatus);
        assertEquals("Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", responseHeader);
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
    }

    @Test
    public void submitSlashTargetRequest() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");

        String request = "GET / HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = connectionBuilder(connection);
        byte[] result = builder.getResponse();
        String responseStatus = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray(responseStatus, responseHeader);

        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", responseStatus);
        assertEquals("Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", responseHeader);
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
    }

    @Test
    public void submitIndexTargetRequest() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");

        String request = "GET /index.html HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = connectionBuilder(connection);
        byte[] result = builder.getResponse();
        String responseStatus = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray(responseStatus, responseHeader);

        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", responseStatus);
        assertEquals("Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", responseHeader);
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
    }

    @Test
    public void garbageMethod() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        String request = "GET Leo HTTP/1.1\r\n\r\n";
        String errorMsg = "<h1>The page you are looking for is 93 million miles away!</h1>";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        Request requestMap = connection.getParser().parse(request.getBytes());
        builder = connectionBuilder(connection);
        byte[] result = builder.getResponse();
        String responseStatus = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray(responseStatus, responseHeader);

        assertThrows(ExceptionInfo.class, () -> {
            connection.getRouter().route(requestMap);
        });
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 404 page not found\r\n", responseStatus);
        assertTrue(responseHeader.contains("Content-Length: " + errorMsg.length()));
        assertEquals(errorMsg, responseBodyMsg);
    }
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

