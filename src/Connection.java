import java.net.Socket;

public interface Connection extends Runnable {
    void run(Socket socket);
    void stop() throws InterruptedException;
}
