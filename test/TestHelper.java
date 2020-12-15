import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelper {
    private final int port;
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
    private ResponseBuilder builder;

    public TestHelper(int port) throws IOException {
        this.port = port;
    }

    public void setResource(String resource) throws IOException {
        String fileRoot = root + resource;
        path = Paths.get(fileRoot);
        File file = new File(fileRoot);
        if (file.isFile()) {
            body = Files.readAllBytes(path);
            contentLength = body.length;
        } else {
            File directory = new File(root + resource);
            File[] files = directory.listFiles();
            String bodyMsg = "<ul>";
            for (File dFile : files) {
                if (dFile.isDirectory())
                    bodyMsg = bodyMsg + "<li><a href=\"" + resource + "/listing/" + dFile.getName() + "\">" + dFile.getName() + "</a></li>";
                else
                    bodyMsg = bodyMsg + "<li><a href=\"" + resource + "/" + dFile.getName() + "\">" + dFile.getName() + "</a></li>";
            }
            bodyMsg = bodyMsg + "</ul>";
            body = bodyMsg.getBytes();
            contentLength = body.length;
        }

    }

    public void connect() throws IOException {
        socket = new Socket("localhost", port);
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

    byte[] getBody() {
        return body;
    }

    String readResponseBodyResult(byte[] body) throws IOException {
        InputStream bodyStream = new ByteArrayInputStream(body);
        InputStreamReader inputReader = new InputStreamReader(bodyStream);
        BufferedReader reader = new BufferedReader(inputReader);
        String line = reader.readLine();
        String bodyLines = "";
        while (line != null) {
            bodyLines = bodyLines + line;
            line = reader.readLine();
        }
        return bodyLines;
    }

    ResponseBuilder getConnectionBuilder(Connection connection) {
        builder = connection.getResponseBuilder();
        return connection.getResponseBuilder();
    }

    ByteArrayOutputStream getFullTargetOutputArray() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write((getResponseStatus() + getResponseHeader()).getBytes());
        target.write(builder.getBody());
        return target;
    }

    String getResponseStatus() {
        return builder.getStatusLine();
    }

    String getResponseHeader() {
        return builder.getHeaders();
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

