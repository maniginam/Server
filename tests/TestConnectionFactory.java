import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory {
    private final int port;
    private final String path;
    private Socket socket;
    private TestConnection connection;

    public TestConnectionFactory(int port, String path) {
        this.port = port;
        this.path = path;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket) {
        connection = new TestConnection(host, socket);
        return connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
