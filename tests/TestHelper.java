import java.io.IOException;
import java.net.Socket;

public class TestHelper {
    private Socket socket;

    public void connect() throws IOException {
        socket = new Socket("localhost", 3141);
    }

    public Socket getSocket() {
        return socket;
    }
}
