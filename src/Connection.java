public interface Connection extends Runnable {
    void start();
    void stop() throws InterruptedException;

}
