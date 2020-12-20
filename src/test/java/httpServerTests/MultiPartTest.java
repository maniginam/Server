package test.java.httpServerTests;

import main.java.httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.server.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiPartTest {
    private HttpTestHelper helper;
    private TestConnectionFactory connectionFactory;
    private Router router;
    private SocketHost host;
    private OutputStream output;
    private BufferedInputStream buffed;
    private Connection connection;
    private HttpResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        int port = 1988;
        helper = new HttpTestHelper(port);
        router = new Router();
        Server.registerResponders(router, helper.root);
        builder = new HttpResponseBuilder();
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(port, connectionFactory);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (helper.getSocket() != null)
            helper.getSocket().close();
    }

    @Test
    public void submitsPOSTMultipartRequest() throws IOException, InterruptedException {
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        helper.setResource("/img/BruslyDog.jpeg");
        String boundary = "Rex&LeoBoundary";
        String multipart1 = "Content-Disposition: form-data; name=\"file\"; filename=\"BruslyDog.jpeg\r\n" +
                "Content-Type: image/jpeg\r\n\r\n";
        int contentLength = ("--" + boundary + "\r\n"
                + multipart1 + "\r\n"
                + "--" + boundary + "--\r\n").getBytes().length
                + helper.getContentLength();
        String requestHeader = "POST /form HTTP/1.1\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n\r\n";

        ByteArrayOutputStream request = new ByteArrayOutputStream();
        output.write(requestHeader.getBytes());
        output.write(("--" + boundary + "\r\n").getBytes());
        output.write(multipart1.getBytes());
        output.write(helper.getBody());
        output.write(("--" + boundary + "--\r\n\r\n").getBytes());
        buffed.read();
        Thread.sleep(100);

        connection = host.getConnections().get(0);



        String status = builder.getStatusLine();
        String header = builder.getHeaders();
        String body = helper.readResponseBodyResult(builder.getBody());

        assertTrue(connection.getRouter().getResponder() instanceof MultiPartResponder);
        assertTrue(status.contains("HTTP/1.1 200 OK"));
        assertTrue(header.contains("Content-Type: application/octet-stream"));
        assertTrue(body.contains("<h2>POST Form</h2>"));
        assertTrue(body.contains("<li>file name: BruslyDog.jpeg</li>"));
        assertTrue(body.contains("<li>file size: 92990</li>"));
    }
}

