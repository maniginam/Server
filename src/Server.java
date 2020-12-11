import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static String message;
    private SocketHost host;
    private HttpConnectionFactory connectionFactory;

    public static void main(String[] args) throws IOException {
        Map<String, String> argMap = makeArgMap(args);
        if (argMap.containsKey("-h")) {
            setUsage();
            System.out.println(message);
            System.exit(0);
        } else if (argMap.containsKey("-x")) {
            setConfigMessage(argMap);
            System.out.println(message);
            System.exit(0);
        } else {
            setConfigMessage(argMap);
            System.out.println(message);
            startServer(argMap);
        }
    }

    private static void startServer(Map<String, String> args) throws IOException {
        int port = Integer.parseInt(args.get("-p"));
        String path = args.get("-r");
        HttpConnectionFactory connectionFactory = new HttpConnectionFactory(port, path);
        SocketHost host = new SocketHost(port, connectionFactory);
        Responder fileResponder = new FileResponder();
        host.start();
//        host.getConnectionThread().join();
    }

    private static Map<String, String> makeArgMap(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        int numOfArgs = args.length;

        for (int i = 0; i < numOfArgs; i++) {
            if (args[i].matches("-p") || args[i].matches("-r")) {
                int j = i + 1;
                argMap.put(args[i], args[j]);
                i = i + 1;
            } else {
                argMap.put(args[i], null);
            }
        }
        return argMap;
    }

    public static void setUsage() {
        String p = "  -p     Specify the port.  Default is 80.";
        String r = "  -r     Specify the root directory.  Default is the current working directory.";
        String h = "  -h     Print this help message";
        String x = "  -x     Print the startup configuration without starting the server";
        message = p + r + h + x;
    }

    public static void setConfigMessage(Map<String, String> args) throws IOException {
        int port;
        String path;
        String invalidArg;

        invalidArg = validateArgs(args);
        port = setPort(args);
        path = setPath(args);

        if (invalidArg != null)
            message = "Invalid option: " + invalidArg;
        else {
            String name = "Example Server\r\n";
            String portLine = "Running on port: " + port + ".\r\n";
            String filesLine = "Serving files from: " + path;
            message = name + portLine + filesLine;
        }
    }

    private static String validateArgs(Map<String, String> args) {
        String invalidArg = null;
        for (String arg : args.keySet()) {
            if (!(arg.startsWith("-h") || arg.startsWith("-x") || arg.startsWith("-p") || arg.startsWith("-r")))
                invalidArg = arg;
        }
        return invalidArg;
    }

    private static String setPath(Map<String, String> args) throws IOException {
        if (args.containsKey("-r"))
            return new File(".").getCanonicalPath() + "/" + args.get("-r");
        else {
            String path = new File(".").getCanonicalPath();
            args.put("-r", path);
            return path;
        }
    }

    private static int setPort(Map<String, String> args) {
        if (args.containsKey("-p"))
            return Integer.parseInt(args.get("-p"));
        else {
            int port = 80;
            args.put("-p", String.valueOf(port));
            return 80;
        }
    }

    public String getMessage() {
        return message;
    }

    public SocketHost getHost() {
        return host;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
