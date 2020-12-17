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
    private RequestParser parser;
    private OutputStream output;
    private BufferedInputStream buffed;
    private Connection connection;
    private HttpResponseBuilder builder;

    @BeforeEach
    public void setup() throws IOException {
        int port = 7115;
        helper = new HttpTestHelper(port);
        connectionFactory = new TestConnectionFactory(port, helper.root);
        router = new Router();
        Server.registerResponders(router, helper.root);
        host = new SocketHost(port, connectionFactory, router);
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
        parser = new RequestParser(buffed);

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);

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
        parser = new RequestParser(buffed);

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);
        builder = helper.getConnectionBuilder(connection);

        String responseBody = helper.readResponseBodyResult(builder.getBody());
        assertTrue(connection.getRouter().getResponder() instanceof PingResponder);
        assertTrue(responseBody.contains("<h2>Ping</h2>"));
        assertTrue(responseBody.contains("<li>start time: "));
        assertTrue(responseBody.contains("<li>end time: "));
        assertTrue(responseBody.contains("<li>sleep seconds: 1</li>"));
    }
}
