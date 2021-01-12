package httpServerTests;

import httpServer.HttpConnection;
import httpServer.HttpResponseBuilder;
import httpServer.RequestParser;
import server.Connection;
import server.ConnectionFactory;
import server.Router;
import server.SocketHost;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpTestHelper {
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
    private HttpResponseBuilder builder;
    private String type;

    public HttpTestHelper(int port) throws IOException {
        this.port = port;
    }

    public void setResource(String resource) throws IOException {
        String fileRoot = root + resource;
        path = Paths.get(fileRoot);
        File file = new File(fileRoot);
        type = "text/html";
        if (file.isFile()) {
            type = resource.split("\\.")[resource.split("\\.").length - 1];
            if (type.contains("pdf"))
                type = "application/pdf";
            else if (type.contains("html"))
                type = "text/html";
            else if (type.contains("jpg"))
                type = "image/jpeg";
            else type = "image/" + type;
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

    public String readResponseBodyResult(byte[] body) throws IOException {
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

    public ByteArrayOutputStream getTargetResonse() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        target.write(("HTTP/1.1 200 OK\r\n" +
                "Server: Gina's Http Server\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Content-Type: " + type + "\r\n\r\n").getBytes());
        target.write(body);
        return target;
    }

    String getResponseStatus() {
        return "HTTP/1.1 200 OK\r\n";
    }

    String getResponseHeader() {
        return "Server: Gina's Http Server\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Content-Type: " + type + "\r\n\r\n";
    }

    public RequestParser getParser(String request) throws IOException {
        PipedInputStream inputPipe = new PipedInputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();

        inputPipe.connect(outputPipe);
        outputPipe.write(request.getBytes());
        BufferedInputStream buffedInput = new BufferedInputStream(inputPipe);

        return new RequestParser(buffedInput);
    }

    public String getType() {
        return type;
    }
}

class TestConnectionFactory implements ConnectionFactory {

    private final Router router;
    private TestConnection connection;
    private HttpResponseBuilder builder;

    public TestConnectionFactory(Router router, HttpResponseBuilder builder) {
        this.router = router;
        this.builder = builder;

    }

    @Override
    public TestConnection createConnection(SocketHost host, Socket socket) throws IOException {
        connection = new TestConnection(host, socket, router, builder);
        return connection;
    }

    @Override
    public TestConnection getConnection() {
        return connection;
    }

}

class TestConnection extends HttpConnection {

    public TestConnection(SocketHost host, Socket socket, Router router, HttpResponseBuilder builder) throws IOException {
        super(host, socket, router);
    }
}

