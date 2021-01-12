package httpServerTests;

import httpServer.HttpResponseBuilder;
import httpServer.PingResponder;
import httpServer.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Connection;
import server.Responder;
import server.Router;
import server.SocketHost;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        host.end();
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

        Map<String, Object> result = router.getResponseMap();
        String responseMsg = helper.readResponseBodyResult((byte[]) result.get("body"));

        assertTrue(connection.getRouter().getResponder() instanceof PingResponder);
        assertTrue(responseMsg.contains("<h2>Ping</h2>"));
        assertTrue(responseMsg.contains("<li>start time: "));
        assertTrue(responseMsg.contains("<li>end time: "));
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

        Map<String, Object> result = router.getResponseMap();
        String responseMsg = helper.readResponseBodyResult((byte[]) result.get("body"));

        assertTrue(connection.getRouter().getResponder() instanceof PingResponder);
        assertTrue(responseMsg.contains("<h2>Ping</h2>"));
        assertTrue(responseMsg.contains("<li>start time: "));
        assertTrue(responseMsg.contains("<li>end time: "));
        assertTrue(responseMsg.contains("<li>sleep seconds: 1</li>"));
    }

    @Test
    public void pingTwice() throws IOException, InterruptedException {
        String request1 = "GET /ping/2 HTTP/1.1\r\n\r\n";
        String request2 = "GET /ping HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        output.write(request1.getBytes());
        output.write(request2.getBytes());
        buffed.read();

        Map<String, Object> result1 = router.getResponseMap();
        String response1 = helper.readResponseBodyResult((byte[]) result1.get("body"));
        Responder responder1 = router.getResponder();

        Thread.sleep(100);
        Map<String, Object> result2 = router.getResponseMap();
        String response2 = helper.readResponseBodyResult((byte[]) result2.get("body"));
        Responder responder2 = router.getResponder();

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
