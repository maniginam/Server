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
    Map<String, Object> routerResponseMap;
    private Thread thread;
    private RequestParser parser;
    private HttpResponseBuilder builder;
    private OutputStream output;
    private byte[] response;
    private HashMap<String, Object> responseMap;
    private Map<String, Object> request;

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
        Map<String, Object> routerResponseMap;
        try {
            BufferedInputStream buffedInput = new BufferedInputStream(socket.getInputStream());

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    parser = new RequestParser(buffedInput);
                    request = parser.parse();

                    responseMap = new HashMap<>();
                    routerResponseMap = requestToResponse();

                    for (String key : routerResponseMap.keySet())
                    {
                        responseMap.put(key, (routerResponseMap.get(key)));
                    }
                    responseMap.put("Server", "Gina's Http Server");

                    if ((int) responseMap.get("statusCode") > 300 && (int) responseMap.get("statusCode") < 400) {
                        request.remove("resource");
                        request.put("resource", responseMap.get("Location"));

                        Map<String, Object> responseMap2 = requestToResponse();
                        responseMap.put("body", responseMap2.get("body"));
                        responseMap.put("statusCode", responseMap2.get("statusCode"));
                        responseMap.put("Content-Type", responseMap2.get("Content-Type"));
                        responseMap.put("Content-Length", responseMap2.get("Content-Length"));
                    }

                    response = builder.buildResponse(responseMap);
                    if (response != null) {
                        send(response);
                    }

                } else {
                    Thread.sleep(1);
                }
            }
        } catch (IOException | InterruptedException | ExceptionInfo e) {
            e.printStackTrace();
        }
        host.getConnections().remove(this);
    }

    private Map<String, Object> requestToResponse() throws IOException, InterruptedException {

        try {
            routerResponseMap = router.route(request);
        } catch (ExceptionInfo e) {
            routerResponseMap = e.getResponse();
        }
        return routerResponseMap;
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
