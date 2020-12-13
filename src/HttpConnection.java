import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private final Router router;
    private Thread thread;
    private RequestParser parser;
    private ResponseBuilder builder;
    private Responder responder;

    public HttpConnection(SocketHost host, Socket socket, Router router) {
        this.host = host;
        this.socket = socket;
        this.router = router;
        parser = new RequestParser();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedInputStream binput = new BufferedInputStream(input);
            boolean isHeaderComplete = parser.isHeaderComplete();

            while(host.isRunning() && socket.isConnected()) {
                RequestParser parser = new RequestParser();
                if (binput.available() > 0) {
                    ByteArrayOutputStream outputHeader = new ByteArrayOutputStream();
                    Request request = null;
                    while(!isHeaderComplete) {
                        outputHeader.write(binput.read());
                        byte[] output = outputHeader.toByteArray();
                        request = parser.parse(output);
                        isHeaderComplete = parser.isHeaderComplete();
                    }
                    router.route(request);
                } else Thread.sleep(1);
            }
        } catch (IOException | ExceptionInfo | InterruptedException e) {
            e.printStackTrace();
        }
        host.getConnections().remove(this);
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    public Thread getThread() {
        return thread;
    }

    public RequestParser getParser() {
        return parser;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return builder;
    }
}
