package serverTests;

import httpServer.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private TestHelper helper;
    private Map<String, String> args;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper(1003);
        args = new HashMap<>();
    }

    @Test
    public void submitInvalidEntry() throws IOException {
        String target = "Invalid option: Rex";
        args.put("Rex", null);
        Server.setConfigMessage(args);
        String result = Server.message;
        assertEquals(target, result);
    }

    @Test
    public void submitH() {
        String target = "  -p     Specify the port.  Default is 80." +
                "  -r     Specify the root directory.  Default is the current working directory." +
                "  -h     Print this help message" +
                "  -x     Print the startup configuration without starting the server";

        Server.setUsage();
        String result = Server.message;

        assertEquals(target, result);
    }

    @Test
    public void submitXEntry() throws IOException, InterruptedException {
        String target = Server.serverName + "\r\n" +
                "Running on port: 80.\r\n" +
                "Serving files from: " + new File(".").getCanonicalPath();
        args.put("-x", null);

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(target, result);
    }

    @Test
    public void submitPEntryWithX() throws Exception {
        String target = Server.serverName + "\r\n" +
                "Running on port: 3141.\r\n" +
                "Serving files from: " + new File(".").getCanonicalPath();
        args.put("-x", null);
        args.put("-p", "3141");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(target, result);

    }

    @Test
    public void submitREntry() throws Exception {
        String target = Server.serverName + "\r\n" +
                "Running on port: 80.\r\n" +
                "Serving files from: " + new File(".").getCanonicalPath() + "/testroot";

        args.put("-x", null);
        args.put("-r", "testroot");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(target, result);
    }

    @Test
    public void messageOnlyFromPEntryWithNOX() throws Exception {
        String target = Server.serverName + "\r\n" +
                "Running on port: 3141.\r\n" +
                "Serving files from: " + new File(".").getCanonicalPath();
        
        args.put("-p", "3141");

        Server.setConfigMessage(args);
        String result = Server.message;

        assertEquals(target, result);
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
