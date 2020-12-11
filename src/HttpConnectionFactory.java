import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {
    private final String path;
    private final int port;
    private Socket socket;
    private HttpConnection connection;

    public HttpConnectionFactory(int port, String path) {
        this.port = port;
        this.path = path;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket) {
        connection = new HttpConnection(host, socket);
        return connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
