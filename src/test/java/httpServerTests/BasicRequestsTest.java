package httpServerTests;

import httpServer.FileResponder;
import httpServer.HttpResponseBuilder;
import httpServer.RequestParser;
import httpServer.Server;
import server.Connection;
import server.ExceptionInfo;
import server.Router;
import server.SocketHost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BasicRequestsTest {
    private HttpTestHelper helper;
    private TestConnectionFactory connectionFactory;
    private Router router;
    private SocketHost host;
    private OutputStream output;
    private BufferedInputStream buffed;
    private Connection connection;
    private HttpResponseBuilder builder;
    private RequestParser parser;

    @BeforeEach
    public void setup() throws IOException {
        helper = new HttpTestHelper(1518);
        router = new Router();
        Server.registerResponders(router, helper.root);
        builder = new HttpResponseBuilder();
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(1518, connectionFactory);
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

    private ByteArrayOutputStream getFullTargetOutputArray() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write((getResponseStatus() + getResponseHeader()).getBytes());
        target.write(builder.getBody());
        return target;
    }

    private String getResponseStatus() {
        return builder.getStatusLine();
    }

    private String getResponseHeader() {
        return builder.getHeaders();
    }

    @Test
    public void submitBlankTargetRequest() throws IOException, ExceptionInfo, InterruptedException {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");
        parser = new RequestParser(buffed);

        String request = "GET HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        byte[] result = builder.getResponse();
        String responseBodyMsg = helper.readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", builder.getStatusLine());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", getResponseHeader());
        assertTrue(responseBodyMsg.contains("<h1>Hello, World!</h1>"));
        assertTrue(responseBodyMsg.contains("<p>You have reached the index.html file in testroot of the httpServer-spec project.</p>"));
    }

    @Test
    public void submitSlashTargetRequest() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");
        parser = new RequestParser(buffed);

        String request = "GET / HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        byte[] result = builder.getResponse();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", getResponseHeader());
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
    }

    @Test
    public void submitIndexTargetRequest() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/index.html");
        parser = new RequestParser(buffed);

        String request = "GET /index.html HTTP/1.1\r\n\r\n";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        byte[] result = builder.getResponse();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", getResponseHeader());
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
    }

    @Test
    public void throwExceptionForLeoResource() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        parser = new RequestParser(buffed);

        String request = "GET /Leo HTTP/1.1\r\n\r\n";
        String errorMsg = "<h1>The page you are looking for is 93 million miles away!</h1>";
        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("method", "GET");
        requestMap.put("resource", "/Leo");
        requestMap.put("httpVersion", "HTTP/1.1");
        byte[] result = builder.getResponse();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertThrows(ExceptionInfo.class, () -> {
            connection.getRouter().route(requestMap);
        });
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 404 page not found\r\n", getResponseStatus());
        assertTrue(getResponseHeader().contains("Content-Length: " + errorMsg.length()));
        assertTrue(getResponseHeader().contains("Server: Gina's Http Server"));
        assertEquals(errorMsg, responseBodyMsg);
    }

    @Test
    public void throwExceptionForRexMethod() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        parser = new RequestParser(buffed);

        String request = "REX /index.html HTTP/1.1\r\n\r\n";
        String errorMsg = "<h1>The page you are looking for is 93 million miles away!  And the method REX you requested is not valid!</h1>";
        output.write(request.getBytes());
        buffed.read();

        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        RequestParser parser = new RequestParser(buffedInput);

        assertThrows(ExceptionInfo.class, () -> {
            parser.parse();
        });

        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        byte[] result = builder.getResponse();
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 404 page not found\r\n", getResponseStatus());
        assertTrue(getResponseHeader().contains("Content-Length: " + errorMsg.length()));
        assertTrue(getResponseHeader().contains("Server: Gina's Http Server"));
        assertEquals(errorMsg, responseBodyMsg);
    }

}
