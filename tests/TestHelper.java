import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelper {
    private Socket socket;
    private Connection connection;
    private ConnectionFactory connectionFactory;
    private InputStream input;
    private BufferedInputStream buffed;
    private OutputStream output;

    String pathName = new File(".").getCanonicalPath() + "/testroot/index.html";
    Path path = Paths.get(pathName);
    File file = new File(String.valueOf(path));
    byte[] body = Files.readAllBytes(path);
    int contentLength = body.length;

    public TestHelper() throws IOException {
    }

    public void connect() throws IOException {
        socket = new Socket("localhost", 3141);
        input = socket.getInputStream();
        buffed = new BufferedInputStream(input);
        output = socket.getOutputStream();
    }

    public Socket getSocket() { return socket; }
    public OutputStream getOutput() {
        return output;
    }
    public BufferedInputStream getBuffedInput() { return buffed; }
}

class TestConnectionFactory implements ConnectionFactory {
    private final int port;
    private final String path;
    private TestConnection connection;

    public TestConnectionFactory(int port, String path) {
        this.port = port;
        this.path = path;

    }

    @Override
    public TestConnection createConnection(SocketHost host, Socket socket) {
        connection = new TestConnection(host, socket);
        return connection;
    }

    @Override
    public TestConnection getConnection() {
        return connection;
    }

}

class TestConnection extends HttpConnection {

    public TestConnection(SocketHost host, Socket socket) {
        super(host, socket);
    }
}

