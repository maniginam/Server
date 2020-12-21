package serverTests;

import httpServer.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
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
    public void regex() {
        Pattern fileRegEx = Pattern.compile("\\..{3,4}$");
        Pattern listRegEx = Pattern.compile("\\w[^(ping)|(form)]+");
        Pattern pingRegEx = Pattern.compile("ping");
        Pattern formRegEx = Pattern.compile("form\\?+");
        Pattern postFormRegEx = Pattern.compile("form$");

        assertTrue(fileRegEx.matcher("/hello.html").find());
        assertTrue(fileRegEx.matcher("/hello/helloagain.jpg").find());
        assertFalse(fileRegEx.matcher("/hello.hl").find());
        assertFalse(fileRegEx.matcher("/hello").find());

        assertTrue(listRegEx.matcher("/hello").find());
        assertTrue(listRegEx.matcher("/hello/helloagain").find());
//        assertFalse(listRegEx.matcher("/hello.hl").find());
        assertFalse(listRegEx.matcher("/ping").find());
        assertFalse(listRegEx.matcher("/form").find());

        assertTrue(pingRegEx.matcher("/ping").find());
        assertFalse(pingRegEx.matcher("/hello").find());

        assertTrue(formRegEx.matcher("/form?").find());
        assertFalse(formRegEx.matcher("/form").find());

        assertTrue(postFormRegEx.matcher("/form").find());
        assertFalse(postFormRegEx.matcher("/form?").find());


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