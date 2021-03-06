package serverTests;

import server.Connection;
import server.ResponseBuilder;
import server.Router;
import server.SocketHost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SocketHostTest {
    private TestHelper helper;
    private TestConnectionFactory connectionFactory;
    private SocketHost host;
    private Socket socket;
    private Router router;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper(1003);
        router = new Router();
        ResponseBuilder builder = new TestResponseBuilder();
        connectionFactory = new TestConnectionFactory(router, builder);
        host = new SocketHost(3141, connectionFactory);
    }

    private void connect() throws IOException {
        socket = new Socket("localhost", 3141);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.end();
        if (socket != null)
            socket.close();
    }

    @Test
    public void checkPort() {
        assertEquals(3141, host.port);
    }

    @Test
    public void isConnected() throws Exception {
        host.start();
        connect();
        assertTrue(socket.isConnected());
    }

    @Test
    public void startStop() throws Exception {
        assertFalse(host.isRunning());

        host.start();
        assertTrue(host.isRunning());

        host.end();
        assertFalse(host.isRunning());
    }

    @Test
    public void twoConnects() throws IOException, InterruptedException {
        host.start();
        connect();
        connect();
        Thread.sleep(100);

        int connectionCount = host.getConnections().size();

        assertEquals(2, connectionCount);
    }

    @Test
    public void cleanClose() throws Exception {
        host.start();
        assertTrue(host.isAlive());
        connect();
        Thread.sleep(100);
        host.end();
        assertFalse(host.isAlive());
        List<Connection> connections = host.getConnections();
        for (Connection connection : connections) {
            assertFalse(connection.getThread().isAlive());
        }
    }
}

