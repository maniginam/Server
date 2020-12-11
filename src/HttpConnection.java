import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpConnection implements Connection {
    private final Socket socket;
    private final SocketHost host;
    private Thread thread;
    private RequestParser parser;

    public HttpConnection(SocketHost host, Socket socket) {
        this.host = host;
        this.socket = socket;
        parser = new RequestParser();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        RequestParser parser = new RequestParser();
//        try {
//            InputStream input = socket.getInputStream();
//            BufferedInputStream binput = new BufferedInputStream(input);
//            boolean isHeaderComplete = parser.isHeaderComplete();
//
//            while(host.isRunning() && socket.isConnected()) {
//                if (binput.available() > 0) {
//                    byte[] marker = "\r\n\r\n".getBytes();
//                    ByteArrayOutputStream outputHeader = new ByteArrayOutputStream();
//                    while(!isHeaderComplete)
//                        outputHeader.write(binput.read());
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    public Thread getThread() {
        return thread;
    }

    public RequestParser getParser() {
        return null;
    }
}
