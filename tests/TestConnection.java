import java.net.Socket;

public class TestConnection implements Connection {
    private final SocketHost host;
    private final Socket socket;
    private Thread thread;

    public TestConnection(SocketHost host, Socket socket) {
        this.host = host;
        this.socket = socket;
    }

    @Override
    public void run() {
    }

    @Override
    public void stop() throws InterruptedException {
    }

}
