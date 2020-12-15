import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FilesTests {

    private TestHelper helper;
    private TestConnectionFactory connectionFactory;
    private Router router;
    private SocketHost host;
    private RequestParser parser;
    private OutputStream output;
    private BufferedInputStream buffed;
    private Connection connection;
    private ResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper(1003);
        connectionFactory = new TestConnectionFactory(1003, helper.root);
        router = new Router();
        Server.registerResponders(router, helper.root);
        host = new SocketHost(1003, connectionFactory, router);
        parser = new RequestParser();
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (helper.getSocket() != null)
            helper.getSocket().close();
    }

    private ResponseBuilder getConnectionBuilder(Connection connection) {
        return connection.getResponseBuilder();
    }

    @Test
    public void listsAllFilesInListing() throws IOException, ExceptionInfo {
        String request = "GET /listing HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ListingResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", helper.getResponseHeader());
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<ul>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/index.html\">index.html</a></li>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/hello.pdf\">hello.pdf</a></li>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/listing/img\">img</a></li>"));
    }

    @Test
    public void listsAllImgsInListingImg() throws IOException, ExceptionInfo {
        String request = "GET /listing/img HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ListingResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", helper.getResponseHeader());
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<ul>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/BruslyDog.jpeg\">BruslyDog.jpeg</a></li>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/decepticon.png\">decepticon.png</a></li>"));
        assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/HurricaneBabies.jpeg\">HurricaneBabies.jpeg</a></li>"));
    }

    @Test
    public void jpegRequest() throws IOException, ExceptionInfo {
        String request = "GET /img/BruslyDog.jpeg HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/BruslyDog.jpeg");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ImageResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: image/jpeg\r\n\r\n", helper.getResponseHeader());
        assertArrayEquals(helper.getBody(), responseBody);
    }

    @Test
    public void jpgRequest() throws IOException, ExceptionInfo {
        String request = "GET /img/autobot.jpg HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/autobot.jpg");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ImageResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: image/jpeg\r\n\r\n", helper.getResponseHeader());
        assertArrayEquals(helper.getBody(), responseBody);
    }

    @Test
    public void pngRequest() throws IOException, ExceptionInfo {
        String request = "GET /img/decepticon.png HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/decepticon.png");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ImageResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: image/png\r\n\r\n", helper.getResponseHeader());
        assertArrayEquals(helper.getBody(), responseBody);
    }

    @Test
    public void pdfRequest() throws IOException, ExceptionInfo {
        String request = "GET /hello.pdf HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/hello.pdf");

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);
        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertArrayEquals(target.toByteArray(), result);
        assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: application/pdf\r\n\r\n", helper.getResponseHeader());
        assertArrayEquals(helper.getBody(), responseBody);
    }
}
