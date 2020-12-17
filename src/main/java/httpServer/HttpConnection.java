package main.java.httpServer;

import main.java.server.*;

import java.io.*;
import java.net.Socket;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private final Router router;
    private Thread thread;
    private RequestParser parser;
    private HttpResponseBuilder builder;
    private OutputStream output;
    private Response responseMap;
    private boolean headerDone;

    public HttpConnection(SocketHost host, Socket socket, Router router) throws IOException {
        this.host = host;
        this.socket = socket;
        this.router = router;
        output = socket.getOutputStream();
        builder = new HttpResponseBuilder();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        builder = new HttpResponseBuilder();

        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());
            boolean doneParsing = false;

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    Request request = new Request();
                    parser = new RequestParser(buffedInput);
                    request = parser.parse();
                    ByteArrayOutputStream requestHeader = new ByteArrayOutputStream();
                    byte[] requestBytes = null;

                    try {
                        while (!headerDone) {
                            requestHeader.write(buffedInput.read());
                            requestBytes = requestHeader.toByteArray();
                            request = parser.parse();
                            headerDone = getParser().isHeaderComplete();
                        }
                        if (getParser().getIsMultiPartRequest()) {
                            int contentLength;
                            if (request != null)
                                contentLength = Integer.parseInt((String) request.get("Content-Length"));
                            else contentLength = 0;
                            byte[] body = new byte[contentLength];
                            for (int i = 0; i < contentLength; i++) {
                                body[i] = (byte) buffedInput.read();
                            }
                            parser.parseMultiPart(body);
                        }

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
        } catch (IOException | InterruptedException | ExceptionInfo e) {
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


    public HttpResponseBuilder getResponseBuilder() {
        return builder;
    }

}
