package httpServer;

import server.ExceptionInfo;
import server.ResponseBuilder;
import server.Router;
import server.SocketHost;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Server {
    public static String message;
    private static int port;
    private static String root;
    private static String serverName = "Gina's Http Server";

    public static void main(String[] args) throws IOException, InterruptedException {
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
            startServer();
        }
    }


    private static void startServer() throws IOException, InterruptedException {
        Router router = new Router();
        HttpResponseBuilder builder = new HttpResponseBuilder();
        registerResponders(router, builder, root);
        HttpConnectionFactory connectionFactory = new HttpConnectionFactory(router, builder);
        SocketHost host = new SocketHost(port, connectionFactory);
        host.start();
        host.join();
    }

    public static void registerResponders(Router router, ResponseBuilder builder, String root) {
        ExceptionInfo.serverName = serverName;
        ExceptionInfo.builder = builder;
        Pattern fileRegEx = Pattern.compile("\\..{3,4}$");
        Pattern listRegEx = Pattern.compile("/listing+(\\/\\w)*");
        Pattern pingRegEx = Pattern.compile("ping{1}(\\/\\d)*");
        Pattern formRegEx = Pattern.compile("form\\?+");
        Pattern postFormRegEx = Pattern.compile("form$");

        router.registerResponder("GET", fileRegEx, new FileResponder(serverName, root));
        router.registerResponder("GET", listRegEx, new ListingResponder(serverName, root));
        router.registerResponder("GET", pingRegEx, new PingResponder(serverName));
        router.registerResponder("GET", formRegEx, new FormResponder(serverName));
        router.registerResponder("POST", postFormRegEx, new MultiPartResponder(serverName));
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
        String invalidArg;

        invalidArg = validateArgs(args);
        port = setPort(args);
        root = setRoot(args);

        if (invalidArg != null)
            message = "Invalid option: " + invalidArg;
        else {
            String name = "Example http.Server\r\n";
            String portLine = "Running on port: " + port + ".\r\n";
            String filesLine = "Serving files from: " + root;
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

    private static String setRoot(Map<String, String> args) throws IOException {
        if (args.containsKey("-r"))
            return new File(".").getCanonicalPath() + "/" + args.get("-r");
        else {
            String root = new File(".").getCanonicalPath();
            args.put("-r", root);
            return root;
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

}