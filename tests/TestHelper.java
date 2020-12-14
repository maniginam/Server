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
    private BufferedReader reader;
    private ByteArrayOutputStream outBytes;
    String root = new File(".").getCanonicalPath() + "/testroot";
    private Path path;
    private byte[] body;
    private int contentLength;

    public TestHelper() throws IOException {
    }

    public void setResource(String resource) throws IOException {
        String fileRoot = root + resource;
        path = Paths.get(fileRoot);
        body = Files.readAllBytes(path);
        contentLength = body.length;

    }


    public void connect() throws IOException {
        socket = new Socket("localhost", 3141);
        input = socket.getInputStream();
        buffed = new BufferedInputStream(input);
        reader = new BufferedReader(new InputStreamReader(buffed));
        output = socket.getOutputStream();
        outBytes = new ByteArrayOutputStream();
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

    public int getContentLength() {
        return contentLength;
    }

    public byte[] getBody() {
        return body;
    }
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
    public TestConnection createConnection(SocketHost host, Socket socket, Router router) throws IOException {
        connection = new TestConnection(host, socket, router);
        return connection;
    }

    @Override
    public TestConnection getConnection() {
        return connection;
    }

}

class TestConnection extends HttpConnection {

    public TestConnection(SocketHost host, Socket socket, Router router) throws IOException {
        super(host, socket, router);
    }
}

