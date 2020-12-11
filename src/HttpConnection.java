import java.net.Socket;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private Thread thread;

    public HttpConnection(SocketHost host, Socket socket) {
        this.host = host;
        this.socket = socket;
    }

    @Override
    public void run() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }
}
