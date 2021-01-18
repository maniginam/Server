package serverTests;

import server.*;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class TestHelper {
    private final int port;
    private Socket socket;
    private InputStream input;
    private BufferedInputStream buffed;
    private OutputStream output;
    private BufferedReader reader;
    String root = new File(".").getCanonicalPath() + "/serverFiles";

    public TestHelper(int port) throws IOException {
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket("localhost", port);
        input = socket.getInputStream();
        buffed = new BufferedInputStream(input);
        reader = new BufferedReader(new InputStreamReader(buffed));
        output = socket.getOutputStream();
    }

    public Socket getSocket() {
        return socket;
    }

    public OutputStream getOutput() {
        return output;
    }

    public BufferedInputStream getBuffedInput() {
        return buffed;
    }

    public BufferedReader getReader() {
        return reader;
    }
}


class TestConnectionFactory implements ConnectionFactory {
    private final Router router;
    private final ResponseBuilder builder;
    private TestConnection connection;

    public TestConnectionFactory(Router router, ResponseBuilder builder) {
        this.router = router;
        this.builder = builder;
    }

    @Override
    public TestConnection createConnection(SocketHost host, Socket socket) throws IOException {
        connection = new TestConnection(host, socket, router);
        return connection;
    }

    @Override
    public TestConnection getConnection() {
        return connection;
    }

}

class TestConnection implements Connection {

    private final SocketHost host;
    private final Socket socket;
    private final Router router;
    private Thread thread;

    public TestConnection(SocketHost host, Socket socket, Router router) {
        this.host = host;
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public Router getRouter() {
        return null;
    }

    @Override
    public Map<String, Object> getResponseMap() {
        return null;
    }

    @Override
    public void run() {
    }
}
