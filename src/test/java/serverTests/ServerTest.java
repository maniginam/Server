package test.java.serverTests;

import main.java.server.*;
import main.java.httpServer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        helper = new TestHelper(1003);
    }

    @Test
    public void submitInvalidEntry() throws IOException {
        String message = "Invalid option: Rex";
        Map<String, String> args = new HashMap<>();
        args.put("Rex", null);
        Server.setConfigMessage(args);
        String result = Server.message;
        assertEquals(message, result);
    }

    @Test
    public void submitH() {
        String p = "  -p     Specify the port.  Default is 80.";
        String r = "  -r     Specify the root directory.  Default is the current working directory.";
        String h = "  -h     Print this help message";
        String x = "  -x     Print the startup configuration without starting the server";
        String message = p + r + h + x;

        Server.setUsage();
        String result = Server.message;

        assertEquals(message, result);
    }

    @Test
    public void submitXEntry() throws IOException, InterruptedException {
        String name = "Example http.Server\r\n";
        String portLine = "Running on port: 80.\r\n";
        String filesLine = "Serving files from: " + new File(".").getCanonicalPath();
        String message = name + portLine + filesLine;
        Map<String, String> args = new HashMap<>();
        args.put("-x", null);

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(message, result);
    }

    @Test
    public void submitPEntryWithX() throws Exception {
        String name = "Example http.Server\r\n";
        String portLine = "Running on port: 3141.\r\n";
        String filesLine = "Serving files from: " + new File(".").getCanonicalPath();
        String message = name + portLine + filesLine;
        Map<String, String> args = new HashMap<>();
        args.put("-x", null);
        args.put("-p", "3141");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(message, result);

    }

    @Test
    public void submitREntry() throws Exception {
        String name = "Example http.Server\r\n";
        String portLine = "Running on port: 80.\r\n";
        String filesLine = "Serving files from: /Users/maniginam/server/testroot";
        String message = name + portLine + filesLine;

        Map<String, String> args = new HashMap<>();
        args.put("-x", null);
        args.put("-r", "testroot");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(message, result);
    }

    @Test
    public void messageOnlyFromPEntryWithNOX() throws Exception {
        String name = "Example http.Server\r\n";
        String portLine = "Running on port: 3141.\r\n";
        String filesLine = "Serving files from: /Users/maniginam/server";
        String message = name + portLine + filesLine;
        Map<String, String> args = new HashMap<>();
        args.put("-p", "3141");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(message, result);
    }

    @Test
    public void registersFileResponder() throws IOException, ExceptionInfo, InterruptedException {
        Router router = new Router();
        Server.registerResponders(router, helper.root);
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("method", "GET");
        request.put("resource", "/index.html");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof FileResponder);
    }


//    @Test
//    public void callMain() throws IOException {
//        String[] args = new String[0];
//        Map<String, String> argMap = new HashMap<>();
//
//        server.main(args);
//        String result = server.getMessage();
//
//        server.setConfigMessage(argMap);
//        String target = server.getMessage();
//
//        assertEquals(target, result);
//    }

//    @Test
//    public void callMainWithpAndr() throws IOException {
//        String[] args = new String[4];
//        args[0] = "-p";
//        args[1] = "3141";
//        args[2] = "-r";
//        args[3] = "/testroot";
//        Map<String, String> argMap = new HashMap<>();
//        argMap.put("-p", "3141");
//        argMap.put("-r", "/testroot");
//
//        server.main(args);
//        String result = server.getMessage();
//
//        server.setConfigMessage(argMap);
//        String target = server.getMessage();
//
//        assertEquals(target, result);
//    }
}
