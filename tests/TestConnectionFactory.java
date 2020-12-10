import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory {
    private final int port;
    private final String path;
    private Socket socket;

    public TestConnectionFactory(int port, String path) {
        this.port = port;
        this.path = path;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket) {
        Connection connection = new TestConnection(host, socket);
        return connection;
    }
}
