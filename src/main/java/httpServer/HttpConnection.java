package httpServer;

import server.Connection;
import server.ExceptionInfo;
import server.Router;
import server.SocketHost;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private final Router router;
    private Thread thread;
    private RequestParser parser;
    private HttpResponseBuilder builder;
    private OutputStream output;
    private byte[] response;
    private HashMap<String, Object> responseMap;

    public HttpConnection(SocketHost host, Socket socket, Router router) throws IOException {
        this.host = host;
        this.socket = socket;
        this.router = router;
        builder = new HttpResponseBuilder();
        output = socket.getOutputStream();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // COMPLETE TODO: 1/12/21 This guy should be adding the name of server to give to responsebuilder that he creates (not the responders)
        // TODO: 1/13/21 ASK ABOUT NEW RESPONSEMAP & WHY CLOJURE WON'T PUT 
        Map<String, Object> routerResponseMap;
        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    parser = new RequestParser(buffedInput);
                    try {
                        Map<String, Object> request = parser.parse();
                        routerResponseMap = router.route(request);
                    } catch (ExceptionInfo e) {
                        routerResponseMap = e.getResponse();
                    }
                    responseMap = new HashMap<>();
                    for (String key : routerResponseMap.keySet())
                        {
                        responseMap.put(key, (routerResponseMap.get(key)));
                    }
                    responseMap.put("Server", "Gina's Http Server");

                    response = builder.buildResponse(responseMap);
                    if (response != null) {
                        send(response);
                    }

                } else {
                    Thread.sleep(1);
                }
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

    public Map<String, Object> getResponseMap() {
        return responseMap;
    };
}
