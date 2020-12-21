package httpServer;

import server.*;

import java.io.IOException;
import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {
    private final Router router;
    private final HttpResponseBuilder builder;
    private HttpConnection connection;

    public HttpConnectionFactory(Router router, HttpResponseBuilder builder) {
        this.router = router;
        this.builder = builder;
    }

    @Override
    public Connection createConnection(SocketHost host, Socket socket) throws IOException {
        connection = new HttpConnection(host, socket, router, builder);
        return connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }


}
