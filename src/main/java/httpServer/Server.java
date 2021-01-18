package httpServer;

import server.Router;
import server.SocketHost;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Server {
    public static String message;
    private static int port = 80;
    private static String root;
    private static Map<String, Object> serverMap;

    static {
        try {
            root = new File(".").getCanonicalPath() + "/serverFiles";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String serverName = "Gina's Http Server";
    public static Router router;
    private static SocketHost host;

    public static void main(String[] args) throws IOException, InterruptedException {
        serverMap = new HashMap<>();
        submitArgs(args);
        startServer(port, root);
    }

    public static void submitArgs(String[] args) throws IOException {
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
        }
    }

    public static Map<String, String> makeArgMap(String[] args) {
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
        String invalidArg;
        resetMsgVars();
        invalidArg = validateArgs(args);
        setPort(args);
        setRoot(args);

        if (invalidArg != null)
            message = "Invalid option: " + invalidArg;
        else {
            // TODO: 1/13/21 THIS IS WHY I HAD SERVERNAME GOING THROUGHOUT. If only set in connection, then will not print upon start up.  Not giving it to start up, requires setting it in two places...
            String name = serverName + "\r\n";
            String portLine = "Running on port: " + port + ".\r\n";
            String filesLine = "Serving files from: " + root;
            message = name + portLine + filesLine;
        }
    }

    private static void resetMsgVars() throws IOException {
        port = 80;
        root = new File(".").getCanonicalPath() + "/serverFiles";
    }

    private static String validateArgs(Map<String, String> args) {
        String invalidArg = null;
        for (String arg : args.keySet()) {
            if (!(arg.startsWith("-h") || arg.startsWith("-x") || arg.startsWith("-p") || arg.startsWith("-r")))
                invalidArg = arg;
        }
        return invalidArg;
    }

    public static void startServer(int port, String root) throws IOException, InterruptedException {
        router = new Router();
        serverMap.put("router", router);
        registerResponders(router, root);
        HttpConnectionFactory connectionFactory = new HttpConnectionFactory(router);
        host = new SocketHost(port, connectionFactory);
        serverMap.put("host", host);
        host.start();
        host.join();
        // COMPLETE TODO: 1/12/21 command query violation with servermap.  sepearate serverMap from here.
    }

    public static void registerResponders(Router router, String root) {
        Pattern fileRegEx = Pattern.compile("\\..{3,4}$");
        Pattern listRegEx = Pattern.compile("/listing+(\\/\\w)*");
        Pattern pingRegEx = Pattern.compile("ping{1}(\\/\\d)*");
        Pattern formRegEx = Pattern.compile("^\\/form\\?+");
        Pattern postFormRegEx = Pattern.compile("^\\/form$");
// COMPLETE TODO: 1/12/21 responders should not need serverName; only responseBuilder
        router.registerResponder("GET", fileRegEx, new FileResponder(root));
        router.registerResponder("GET", listRegEx, new ListingResponder(root));
        router.registerResponder("GET", pingRegEx, new PingResponder());
        router.registerResponder("GET", formRegEx, new FormResponder());
        router.registerResponder("POST", postFormRegEx, new MultiPartResponder());
    }

    public static SocketHost getHost() {
        return host;
    }

    public static Router getRouter() {
        return router;
    }

    private static void setRoot(Map<String, String> args) throws IOException {
        if (args.containsKey("-r"))
            root = new File(".").getCanonicalPath() + "/" + args.get("-r");
        else {
            args.put("-r", root);
        }
    }

    private static void setPort(Map<String, String> args) {
        if (args.containsKey("-p"))
            port = Integer.parseInt(args.get("-p"));
        else {
            args.put("-p", String.valueOf(port));
        }
    }

}
