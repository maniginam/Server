package server;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(SocketHost socketHost, Socket socket, Router router) throws IOException;

    Connection getConnection();
}
