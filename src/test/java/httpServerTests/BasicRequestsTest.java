package httpServerTests;

import httpServer.FileResponder;
import httpServer.HttpResponseBuilder;
import httpServer.RequestParser;
import httpServer.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Connection;
import server.ExceptionInfo;
import server.Router;
import server.SocketHost;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        builder = new HttpResponseBuilder();
        Server.registerResponders(router, helper.root);
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(1518, connectionFactory);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.end();
        if (helper.getSocket() != null)
            helper.getSocket().close();
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
        Map<String, Object> responseMapResult = router.getResponseMap();
        byte[] bodyResult = (byte[]) responseMapResult.get("body");
        String responseBodyMsg = helper.readResponseBodyResult(bodyResult);

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(helper.getBody(), bodyResult);
        assertTrue(responseMapResult.containsValue(200));
        assertTrue(connection.getResponseMap().containsValue("Gina's Http Server"));
        assertTrue(responseMapResult.containsValue(helper.getContentLength()));
        assertTrue(responseMapResult.containsValue("text/html"));
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
        Map<String, Object> responseMapResult = router.getResponseMap();
        byte[] bodyResult = (byte[]) responseMapResult.get("body");
        String responseBodyMsg = helper.readResponseBodyResult(bodyResult);

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(helper.getBody(), bodyResult);
        assertTrue(responseMapResult.containsValue(200));
        assertTrue(connection.getResponseMap().containsValue("Gina's Http Server"));
        assertTrue(responseMapResult.containsValue(helper.getContentLength()));
        assertTrue(responseMapResult.containsValue("text/html"));
        assertTrue(responseBodyMsg.contains("<h1>Hello, World!</h1>"));
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
        Map<String, Object> responseMapResult = router.getResponseMap();
        byte[] bodyResult = (byte[]) responseMapResult.get("body");
        String responseBodyMsg = helper.readResponseBodyResult(bodyResult);

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(helper.getBody(), bodyResult);
        assertTrue(responseMapResult.containsValue(200));
        assertTrue(connection.getResponseMap().containsValue("Gina's Http Server"));
        assertTrue(responseMapResult.containsValue(helper.getContentLength()));
        assertTrue(responseMapResult.containsValue("text/html"));
        assertTrue(responseBodyMsg.contains("<h1>Hello, World!</h1>"));
    }

    // TODO: 12/28/20 FIX THESE TESTS
//    @Test
//    public void throwExceptionForLeoResource() throws IOException, ExceptionInfo {
//        host.start();
//        helper.connect();
//        output = helper.getOutput();
//        buffed = helper.getBuffedInput();
//        parser = new RequestParser(buffed);
//
//        String request = "GET /Leo HTTP/1.1\r\n\r\n";
//        String errorMsg = "<h1>The page you are looking for is 93 million miles away!</h1>";
//        output.write(request.getBytes());
//        buffed.read();
//
//        connection = host.getConnections().get(0);
//        byte[] result = router.getResponse();
//        Map<String, Object> requestMap = new HashMap<String, Object>();
//        requestMap.put("method", "GET");
//        requestMap.put("resource", "/Leo");
//        requestMap.put("httpVersion", "HTTP/1.1");
//        String responseBodyMsg = helper.readResponseBodyResult(result);
//        ByteArrayOutputStream target = new ByteArrayOutputStream();
//        target.write(("HTTP/1.1 404 page not found\r\n" +
//                "Server: Gina's Http Server\r\n" +
//                "Content-Length: " + errorMsg.length() + "\r\n" +
//                "Content-Type: text/html\r\n\r\n").getBytes());
//        target.write(errorMsg.getBytes());
//
//
//        assertThrows(ExceptionInfo.class, () -> {
//            router.route(requestMap);
//        });
//        assertArrayEquals(target.toByteArray(), result);
//        assertTrue(responseBodyMsg.contains("HTTP/1.1 404"));
//        assertTrue(responseBodyMsg.contains("Server: Gina's Http Server"));
//        assertTrue(responseBodyMsg.contains("Content-Type: text/html"));
//        assertTrue(responseBodyMsg.contains("Content-Length: " + errorMsg.length()));
//    }
//
//    @Test
//    public void throwExceptionForRexMethod() throws IOException, ExceptionInfo {
//        host.start();
//        helper.connect();
//        output = helper.getOutput();
//        buffed = helper.getBuffedInput();
//        parser = new RequestParser(buffed);
//
//        String request = "REX /index.html HTTP/1.1\r\n\r\n";
//        String errorMsg = "<h1>The page you are looking for is 93 million miles away!  And the method REX you requested is not valid!</h1>";
//        output.write(request.getBytes());
//        buffed.read();
//
//        PipedInputStream inputPipe = new PipedInputStream();
//        PipedOutputStream outputPipe = new PipedOutputStream();
//
//        inputPipe.connect(outputPipe);
//        outputPipe.write(request.getBytes());
//        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);
//
//        RequestParser parser = new RequestParser(buffedInput);
//
//        assertThrows(ExceptionInfo.class, () -> {
//            parser.parse();
//        });
//
//        byte[] result = router.getResponse();
//        String responseBodyMsg = helper.readResponseBodyResult(result);
//        ByteArrayOutputStream target = new ByteArrayOutputStream();
//        target.write(("HTTP/1.1 404 page not found\r\n" +
//                "Server: Gina's Http Server\r\n" +
//                "Content-Length: " + errorMsg.length() + "\r\n" +
//                "Content-Type: text/html\r\n\r\n").getBytes());
//        target.write(errorMsg.getBytes());
//
//        assertArrayEquals(target.toByteArray(), result);
//        assertTrue(responseBodyMsg.contains("HTTP/1.1 404"));
//        assertTrue(responseBodyMsg.contains("Server: Gina's Http Server"));
//        assertTrue(responseBodyMsg.contains("Content-Type: text/html"));
//        assertTrue(responseBodyMsg.contains("Content-Length: " + errorMsg.length()));
//        assertTrue(responseBodyMsg.contains(errorMsg));
//    }

}
