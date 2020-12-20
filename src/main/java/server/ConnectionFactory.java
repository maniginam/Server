package main.java.server;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(SocketHost socketHost, Socket socket) throws IOException;

    Connection getConnection();
}
