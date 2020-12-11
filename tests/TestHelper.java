import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class TestHelper {
    private Socket socket;
    private Connection connection;
    private ConnectionFactory connectionFactory;


    public void connect() throws IOException {
        connectionFactory = new TestConnectionFactory(3141, String.valueOf(new File(".").getCanonicalPath()));
        socket = new Socket("localhost", 3141);
        connection = connectionFactory.getConnection();
    }

    public Socket getSocket() {
        return socket;
    }
}
