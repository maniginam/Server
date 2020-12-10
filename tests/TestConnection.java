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
    public void run(Socket socket) {
        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    @Override
    public void run() {

    }
}
