package httpServerTests;

import httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;


import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class FilesTests {

    private HttpTestHelper helper;
    private TestConnectionFactory connectionFactory;
    private Router router;
    private SocketHost host;
    private OutputStream output;
    private BufferedInputStream buffed;
    private HttpResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        helper = new HttpTestHelper(1003);
        router = new Router();
        Server.registerResponders(router, helper.root);
        builder = new HttpResponseBuilder();
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(1003, connectionFactory);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (helper.getSocket() != null)
            helper.getSocket().close();
    }

    @Test
    public void listsAllFilesInListing() throws IOException {
        String request = "GET /listing HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        if (router.getResponder() instanceof ListingResponder)
            assertTrue(router.getResponder() instanceof ListingResponder);
        else System.out.println("NOT LISTING RESPONDER");
        assertArrayEquals(target.toByteArray(), result);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        Assertions.assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", helper.getResponseHeader());
        Assertions.assertTrue(helper.readResponseBodyResult(responseBody).contains("<ul>"));
        Assertions.assertTrue(helper.readResponseBodyResult(responseBody).contains("<li><a href=\"/index.html\">index.html</a></li>"));
        Assertions.assertTrue(helper.readResponseBodyResult(responseBody).contains("<li><a href=\"/hello.pdf\">hello.pdf</a></li>"));
        Assertions.assertTrue(helper.readResponseBodyResult(responseBody).contains("<li><a href=\"/listing/img\">img</a></li>"));
    }

    @Test
    public void listsAllImgsInListingImg() throws IOException {
        String request = "GET /listing/img HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof ListingResponder);
        assertArrayEquals(target.toByteArray(), result);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", helper.getResponseStatus());
        Assertions.assertEquals("Server: Gina's Http Server\r\n" +
                "Content-Length: " + helper.getContentLength() + "\r\n" +
                "Content-Type: text/html\r\n\r\n", helper.getResponseHeader());
        Assertions.assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<ul>"));
        Assertions.assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/BruslyDog.jpeg\">BruslyDog.jpeg</a></li>"));
        Assertions.assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/decepticon.png\">decepticon.png</a></li>"));
        Assertions.assertTrue(helper.readResponseBodyResult(helper.getBody()).contains("<li><a href=\"/img/HurricaneBabies.jpeg\">HurricaneBabies.jpeg</a></li>"));
    }

    @Test
    public void jpegRequest() throws IOException {
        String request = "GET /img/BruslyDog.jpeg HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/BruslyDog.jpeg");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        String status = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", status);
        Assertions.assertEquals(helper.getResponseHeader(), responseHeader);
        Assertions.assertArrayEquals(helper.getBody(), responseBody);
        assertArrayEquals(target.toByteArray(), result);
    }

    @Test
    public void jpgRequest() throws IOException {
        String request = "GET /img/autobot.jpg HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/autobot.jpg");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        String status = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", status);
        Assertions.assertEquals(helper.getResponseHeader(), responseHeader);
        Assertions.assertArrayEquals(helper.getBody(), responseBody);
        assertArrayEquals(target.toByteArray(), result);
    }

    @Test
    public void pngRequest() throws IOException {
        String request = "GET /img/decepticon.png HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/img/decepticon.png");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        String status = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", status);
        Assertions.assertEquals(helper.getResponseHeader(), responseHeader);
        Assertions.assertArrayEquals(helper.getBody(), responseBody);
        assertArrayEquals(target.toByteArray(), result);
    }

    @Test
    public void pdfRequest() throws IOException {
        String request = "GET /hello.pdf HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();
        helper.setResource("/hello.pdf");

        output.write(request.getBytes());
        buffed.read();

        byte[] result = builder.getResponse();
        byte[] responseBody = builder.getBody();
        String status = builder.getStatusLine();
        String responseHeader = builder.getHeaders();
        ByteArrayOutputStream target = helper.getFullTargetOutputArray();

        assertTrue(router.getResponder() instanceof FileResponder);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", status);
        Assertions.assertEquals(helper.getResponseHeader(), responseHeader);
        Assertions.assertArrayEquals(helper.getBody(), responseBody);
        assertArrayEquals(target.toByteArray(), result);
    }
}
