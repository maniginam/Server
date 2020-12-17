package httpServer;

import server.Connection;
import server.ConnectionFactory;
import server.Router;
import server.SocketHost;

import java.io.IOException;
import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {
    private final String path;
    private final int port;
    private Socket socket;
    private HttpConnection connection;

    public HttpConnectionFactory(int port, String path, Router router) {
        this.port = port;
        this.path = path;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket, Router router) throws IOException {
        connection = new HttpConnection(host, socket, router);
        return connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }


}
