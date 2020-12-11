import java.net.Socket;

public class HttpConnection extends Connection {
    private final Socket socket;
    private final SocketHost host;
    private Thread thread;

    public HttpConnection(SocketHost host, Socket socket) {
        this.host = host;
        this.socket = socket;
    }

}
