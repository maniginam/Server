import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class SocketHost {
    private final ConnectionFactory connectionFactory;
    private final int port;
    private final Router router;
    private ServerSocket server;
    private boolean running;
    private Thread connectorThread;
    private List<Connection> connections;

    public SocketHost(int port, ConnectionFactory connectionFactory, Router router) {
        this.port = port;
        this.connectionFactory = connectionFactory;
        this.router = router;
        connections = new LinkedList<Connection>();
        running = false;
    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        Runnable connector = new Runnable() {
            @Override
            public void run() {
                try {
                    acceptConnections();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        running = true;
        connectorThread = new Thread(connector);
        connectorThread.start();

    }

    private void acceptConnections() throws IOException {
        while (running) {
            try {
                Socket socket = server.accept();
                Connection connection = connectionFactory.createConnection(this, socket, router);
                connection.start();
                connections.add(connection);
            } catch (SocketException e) {
                // closed socket service while waiting for connection
            }
        }
    }

    public void stop() throws IOException, InterruptedException {
        while (running) {
            running = false;
            for (Connection connection : connections) {
                connection.stop();
            }
            server.close();
            connectorThread.join();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Thread getConnectorThread() {
        return connectorThread;
    }
}
