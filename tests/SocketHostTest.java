import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class SocketHostTest {
    private SocketHost host;
    private Socket socket;
    private TestConnectionFactory connectionFactory;
    private TestHelper helper;
    private Connection connection;

    @BeforeEach
    public void setup() throws IOException {
        connectionFactory = new TestConnectionFactory(3141, String.valueOf(new File(".").getCanonicalPath()));
        host = new SocketHost(3141, connectionFactory);
    }

    private void connect() throws IOException {
        socket = new Socket("localhost", 3141);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (socket != null)
            socket.close();
    }

    @Test
    public void checkPort() {
        assertEquals(3141, host.getPort());
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

        host.stop();
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

// TODO: 12/11/20 THIS ONE PASSES ONE OUT OF TEN TIMES
//    @Test
//    public void cleanClose() throws Exception {
//        host.start();
//        assertTrue(host.getConnectorThread().isAlive());
//        connect();
//        host.stop();
//        assertFalse(host.getConnectorThread().isAlive());
//        List<Connection> connections = host.getConnections();
//        for (Connection connection : connections) {
//            assertFalse(connection.getThread().isAlive());
//        }
//    }
}

