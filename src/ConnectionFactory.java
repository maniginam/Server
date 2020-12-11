import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(SocketHost socketHost, Socket socket);

    Connection getConnection();
}
