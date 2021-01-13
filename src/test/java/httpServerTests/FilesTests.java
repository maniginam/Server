package httpServerTests;

import httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;


import java.io.*;
import java.util.Map;

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
        builder = new HttpResponseBuilder();
        Server.registerResponders(router, helper.root);
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(1003, connectionFactory);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.end();
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

        Map<String, Object> result = router.getResponseMap();
        String responseBodyMsg = helper.readResponseBodyResult((byte[]) result.get("body"));

        if (router.getResponder() instanceof ListingResponder)
            assertTrue(router.getResponder() instanceof ListingResponder);
        else System.out.println("NOT LISTING RESPONDER");
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(String.valueOf(helper.getContentLength())));
        assertTrue(result.containsValue("text/html"));
        Assertions.assertTrue(responseBodyMsg.contains("<ul>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/index.html\">index.html</a></li>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/hello.pdf\">hello.pdf</a></li>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/listing/img\">img</a></li>"));
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

        Map<String, Object> result = router.getResponseMap();
        String responseBodyMsg = helper.readResponseBodyResult((byte[]) result.get("body"));

        if (router.getResponder() instanceof ListingResponder)
            assertTrue(router.getResponder() instanceof ListingResponder);
        else System.out.println("NOT LISTING RESPONDER");
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(String.valueOf(helper.getContentLength())));
        assertTrue(result.containsValue("text/html"));
        Assertions.assertTrue(responseBodyMsg.contains("<ul>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/img/BruslyDog.jpeg\">BruslyDog.jpeg</a></li>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/img/decepticon.png\">decepticon.png</a></li>"));
        Assertions.assertTrue(responseBodyMsg.contains("<li><a href=\"/img/HurricaneBabies.jpeg\">HurricaneBabies.jpeg</a></li>"));
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

        Map<String, Object> result = router.getResponseMap();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(helper.getContentLength()));
        assertTrue(result.containsValue("image/jpeg"));
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
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

        Map<String, Object> result = router.getResponseMap();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(helper.getContentLength()));
        assertTrue(result.containsValue("image/jpeg"));
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
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

        Map<String, Object> result = router.getResponseMap();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(helper.getContentLength()));
        assertTrue(result.containsValue("image/png"));
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
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

        Map<String, Object> result = router.getResponseMap();

        assertTrue(router.getResponder() instanceof FileResponder);
        assertTrue(result.containsValue(200));
        assertTrue(result.containsValue(helper.getContentLength()));
        assertTrue(result.containsValue("application/pdf"));
        assertArrayEquals(helper.getBody(), (byte[]) result.get("body"));
    }
}
