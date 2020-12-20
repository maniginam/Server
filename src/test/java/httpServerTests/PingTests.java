package test.java.httpServerTests;

import main.java.httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.server.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PingTests {

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
        int port = 7115;
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
    public void pingRespondsImmediately() throws IOException {
        String request = "GET /ping HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);


        String responseBody = helper.readResponseBodyResult(builder.getBody());

        assertTrue(connection.getRouter().getResponder() instanceof PingResponder);
        assertTrue(responseBody.contains("<h2>Ping</h2>"));
        assertTrue(responseBody.contains("<li>start time: "));
        assertTrue(responseBody.contains("<li>end time: "));
    }

    @Test
    public void pingRespondsAfter1Second() throws IOException {
        String request = "GET /ping/1 HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);


        String responseBody = helper.readResponseBodyResult(builder.getBody());
        assertTrue(connection.getRouter().getResponder() instanceof PingResponder);
        assertTrue(responseBody.contains("<h2>Ping</h2>"));
        assertTrue(responseBody.contains("<li>start time: "));
        assertTrue(responseBody.contains("<li>end time: "));
        assertTrue(responseBody.contains("<li>sleep seconds: 1</li>"));
    }

    @Test
    public void pingTwice() throws IOException, InterruptedException {
        String request1 = "GET /ping/2 HTTP/1.1\r\n\r\n";
        String request2 = "GET /ping HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        output.write(request1.getBytes());
        buffed.read();

        String response1 = helper.readResponseBodyResult(builder.getBody());
        Responder responder1 = router.getResponder();

        output.write(request2.getBytes());
        String response2 = helper.readResponseBodyResult(builder.getBody());
        Responder responder2 = router.getResponder();
        System.out.println("response1 = " + response1);
        System.out.println("response2 = " + response2);

        assertTrue(responder1 instanceof PingResponder);
        assertTrue(response1.contains("<h2>Ping</h2>"));
        assertTrue(response1.contains("<li>start time: "));
        assertTrue(response1.contains("<li>end time: "));
        assertTrue(response1.contains("<li>sleep seconds: 2</li>"));

        assertTrue(responder2 instanceof PingResponder);
        assertTrue(response2.contains("<h2>Ping</h2>"));
        assertTrue(response2.contains("<li>start time: "));
        assertTrue(response2.contains("<li>end time: "));
        assertTrue(response2.contains("<li>sleep seconds: 0</li>"));

    }
}
