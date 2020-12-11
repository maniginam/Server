import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class TestHelper {
    private Socket socket;
    private Connection connection;
    private ConnectionFactory connectionFactory;


//    public void connect() throws IOException {
//        socket = new Socket("localhost", 3141);
//    }

    public Socket getSocket() {
        return socket;
    }

}

class TestConnectionFactory implements ConnectionFactory {
    private final int port;
    private final String path;
    private TestConnection connection;

    public TestConnectionFactory(int port, String path) {
        this.port = port;
        this.path = path;

    }

    @Override
    public TestConnection createConnection(SocketHost socketHost, Socket socket) {
        connection = new TestConnection();
        return connection;
    }

    @Override
    public TestConnection getConnection() {
        return connection;
    }

}

class TestConnection extends Connection {
}

