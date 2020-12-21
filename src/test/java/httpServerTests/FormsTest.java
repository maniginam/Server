package httpServerTests;

import httpServer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormsTest {
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
        int port = 1986;
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
    public void submitsFormsRequest() throws IOException {
        String request = "GET /form?rex=3&leo=1 HTTP/1.1\r\n\r\n";
        host.start();
        helper.connect();
        output = helper.getOutput();
        buffed = helper.getBuffedInput();

        output.write(request.getBytes());
        buffed.read();

        connection = host.getConnections().get(0);

        String status = builder.getStatusLine();
        String header = builder.getHeaders();
        String body = helper.readResponseBodyResult(builder.getBody());

        assertTrue(connection.getRouter().getResponder() instanceof FormResponder);
        assertTrue(status.contains("HTTP/1.1 200 OK"));
        assertTrue(header.contains("Content-Type: text/html"));
        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>rex: 3</li>"));
        assertTrue(body.contains("<li>leo: 1</li>"));
    }

}
