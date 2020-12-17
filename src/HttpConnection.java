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
    private boolean headerDone;

    public HttpConnection(SocketHost host, Socket socket, Router router) throws IOException {
        this.host = host;
        this.socket = socket;
        this.router = router;
        output = socket.getOutputStream();
        builder = new ResponseBuilder();
        parser = new RequestParser();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        builder = new ResponseBuilder();

        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());
            boolean doneParsing = false;

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    ByteArrayOutputStream requestHeader = new ByteArrayOutputStream();
                    ByteArrayOutputStream partHeader = new ByteArrayOutputStream();
                    ByteArrayOutputStream requestBodyBytes = new ByteArrayOutputStream();
                    byte[] requestBytes = null;
                    Request request = null;
                    try {
                        while (!headerDone) {
                                requestHeader.write(buffedInput.read());
                                requestBytes = requestHeader.toByteArray();
                                request = parser.parse(requestBytes);
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
                            requestBodyBytes.flush();
                            requestHeader.flush();
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
