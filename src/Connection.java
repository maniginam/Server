public class Connection implements Runnable {
    public Thread thread;

    @Override
    public void run() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    public Thread getThread() {
        return thread;
    }
}
