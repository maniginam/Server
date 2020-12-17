package test.java.httpServerTests;

import main.java.httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.server.*;

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
        connectionFactory = new TestConnectionFactory(1518, helper.root);
        router = new Router();
        Server.registerResponders(router, helper.root);
        host = new SocketHost(1518, connectionFactory, router);
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

    private HttpResponseBuilder getConnectionBuilder(Connection connection) {
        return connection.getResponseBuilder();
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
        builder = getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        String responseBodyMsg = readResponseBodyResult(builder.getBody());
        ByteArrayOutputStream target = getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", builder.getStatusLine());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", getResponseHeader());
        assertEquals("<h1>Hello, World!</h1>", responseBodyMsg);
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
        builder = getConnectionBuilder(connection);
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
        builder = getConnectionBuilder(connection);
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
        Request requestMap = new Request();
        requestMap.put("method", "GET");
        requestMap.put("resource", "/Leo");
        requestMap.put("httpVersion", "HTTP/1.1");
        builder = getConnectionBuilder(connection);
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

        connection = host.getConnections().get(0);
        builder = getConnectionBuilder(connection);
        Request requestMap = new Request();
        requestMap.put("method", "REX");
        requestMap.put("resource", "/index.html");
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

    public static class ResponderTest {

        private RequestParser parser;
        private Request requestMap;
        private Response response;
        private Responder responder;
        private HttpTestHelper helper;

        @BeforeEach
        public void setup() throws IOException {
            helper = new HttpTestHelper(4321);
            requestMap = new Request();
            response = new Response();
        }

        @Test
        public void responseToBlankGET () throws IOException, ExceptionInfo {
            String request = "GET HTTP/1.1\r\n\r\n";
            String root = helper.root;
            requestMap = parser.parse();
            responder = new FileResponder("Rex's Server", root);
            response = responder.respond(requestMap);
            helper.setResource("/index.html");
            byte[] body = helper.getBody();
            int contentLength = helper.getContentLength();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Length", String.valueOf(contentLength));
            headers.put("Content-Type", "text/html");
            headers.put("Server", "Rex's Server");
            Map<String, Object> target = new HashMap<String, Object>();
            target.put("statusCode", "HTTP/1.1 200 OK");
            target.put("headers", headers);
            target.put("body", body);

            assertEquals(200, response.get("statusCode"));
            assertEquals(headers, response.get("headers"));
            assertArrayEquals(body, (byte[]) response.get("body"));
        }

    }
}

