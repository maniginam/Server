package httpServer;

import server.*;

import java.io.IOException;
import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {
    private final Router router;
    private HttpConnection connection;

    public HttpConnectionFactory(Router router) {
        this.router = router;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket) throws IOException {
        connection = new HttpConnection(host, socket, router);
        return connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }


}
