package main.java.httpServer;

import main.java.server.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private final Router router;
    private Thread thread;
    private RequestParser parser;
    private HttpResponseBuilder builder;
    private OutputStream output;
    private Map<String, Object> responseMap;
    private Thread routerThread;

    public HttpConnection(SocketHost host, Socket socket, Router router, HttpResponseBuilder builder) throws IOException {
        this.host = host;
        this.socket = socket;
        this.router = router;
        this.builder = builder;
        output = socket.getOutputStream();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    parser = new RequestParser(buffedInput);
                    try {
                        Map<String, Object> request = parser.parse();
                        responseMap = router.route(request);
                    } catch (ExceptionInfo e) {
                        responseMap = e.getResponse();
                    }
                    byte[] response = builder.buildResponse(responseMap);
                    if (response != null) {
                        send(response);
                    }

                } else Thread.sleep(1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        host.getConnections().remove(this);
    }

    private void send(byte[] response) throws IOException {
        output.write(response);
        output.flush();
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    public RequestParser getParser() {
        return parser;
    }


    public ResponseBuilder getResponseBuilder() {
        return builder;
    }

}
