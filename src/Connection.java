import java.net.Socket;

public interface Connection extends Runnable {
    void stop() throws InterruptedException;
}
