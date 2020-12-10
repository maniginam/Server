import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SocketHost {
    private final int port;
    private final ConnectionFactory connectionFactory;
    private ServerSocket server;
    private Thread connectionThread;
    private Socket socket;
    private List<Connection> connections;
    private boolean running;

    public SocketHost(int port, ConnectionFactory connectionFactory) {
        this.port = port;
        this.connectionFactory = connectionFactory;
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
        connectionThread = new Thread(connector);
        connectionThread.start();
    }

    private void acceptConnections() {
        try {
            socket = server.accept();
            Connection connection = connectionFactory.createConnection(this, socket);
            connections.add(connection);
            connection.run(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() throws IOException, InterruptedException {
        if (running) {
            server.close();
            running = false;
            for (Connection connection : connections) {
                connection.stop();
            }
            connectionThread.join();
        }
    }
}
