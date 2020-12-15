import java.io.*;
import java.net.Socket;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private final Router router;
    private Thread thread;
    private RequestParser parser;
    private ResponseBuilder builder;
    private OutputStream output;
    private Response responseMap;

    public HttpConnection(SocketHost host, Socket socket, Router router) throws IOException {
        this.host = host;
        this.socket = socket;
        this.router = router;
        output = socket.getOutputStream();
        parser = new RequestParser();
        builder = new ResponseBuilder();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());
            boolean isHeaderComplete = parser.isHeaderComplete();
            builder = new ResponseBuilder();

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    RequestParser parser = new RequestParser();
                    ByteArrayOutputStream requestHeader = new ByteArrayOutputStream();
                    byte[] requestBytes = requestHeader.toByteArray();
                    Request request = null;
                    try {
                        while (!isHeaderComplete) {
                            requestHeader.write(buffedInput.read());
                            requestBytes = requestHeader.toByteArray();
                            request = parser.parse(requestBytes);
                            isHeaderComplete = parser.isHeaderComplete();
                        }
                        responseMap = router.route(request);
                    } catch (ExceptionInfo e) {
                        responseMap = e.getResponse();
                    }
                        byte[] response = builder.buildResponse(responseMap);

                        if (response != null) {
                            send(response);
                            output.flush();
                        }

                    } else Thread.sleep(1);
                }
            } catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
            host.getConnections().remove(this);
        }

        private void send (byte[] response) throws IOException {
            output.write(response);
        }

        public void stop () throws InterruptedException {
            if (thread != null)
                thread.join();
        }

        public Thread getThread () {
            return thread;
        }

        @Override
        public Router getRouter () {
            return router;
        }

        public RequestParser getParser () {
            return parser;
        }

        @Override
        public ResponseBuilder getResponseBuilder () {
            return builder;
        }

    }
