import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.Socket;


import static org.junit.jupiter.api.Assertions.*;

public class SocketHostTest {
    private SocketHost host;
    private Socket socket;
    private ConnectionFactory connectionFactory;
    private TestHelper helper;
    private Connection connection;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        connectionFactory = new TestConnectionFactory(3141, String.valueOf(new File(".").getCanonicalPath()));
        host = new SocketHost(3141, connectionFactory);
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (socket != null)
            socket.close();
    }

    @Test
    public void isConnected() throws Exception {
        host.start();
        helper.connect();
        socket = helper.getSocket();
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
}
