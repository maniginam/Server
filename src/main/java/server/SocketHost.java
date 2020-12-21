package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class SocketHost {
    private final ConnectionFactory connectionFactory;
    public final int port;
    private ServerSocket server;
    private boolean running = false;
    private Thread connectorThread;
    private final List<Connection> connections = new LinkedList<>();

    public SocketHost(int port, ConnectionFactory connectionFactory) {
        this.port = port;
        this.connectionFactory = connectionFactory;
    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        running = true;
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
        connectorThread = new Thread(connector);
        connectorThread.start();
    }

    private void acceptConnections() throws IOException {
        while (running) {
            try {
                Socket socket = server.accept();
                Connection connection = connectionFactory.createConnection(this, socket);
                connections.add(connection);
                connection.start();
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

    public List<Connection> getConnections() {
        return connections;
    }

    public void join() throws InterruptedException {
        connectorThread.join();
    }

    public boolean isAlive() {
        return connectorThread.isAlive();
    }
}
