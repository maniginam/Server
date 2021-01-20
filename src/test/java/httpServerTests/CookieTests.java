//package httpServerTests;
//
//import httpServer.FileResponder;
//import httpServer.HttpResponseBuilder;
//import httpServer.Server;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import server.Router;
//import server.SocketHost;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class CookieTests {
//
//    private HttpTestHelper helper;
//    private Router router;
//    private HttpResponseBuilder builder;
//    private TestConnectionFactory connectionFactory;
//    private SocketHost host;
//    private OutputStream output;
//    private BufferedInputStream buffed;
//
//    @BeforeEach
//    public void setup() throws IOException {
//        helper = new HttpTestHelper(1003);
//        router = new Router();
//        builder = new HttpResponseBuilder();
//        Server.registerResponders(router, helper.root);
//        connectionFactory = new TestConnectionFactory(router, builder);
//        host = new SocketHost(1003, connectionFactory);
//    }
//
//    @AfterEach
//    private void tearDown() throws Exception {
//        host.end();
//        if (helper.getSocket() != null)
//            helper.getSocket().close();
//    }
//
//    @Test
//    public void fileResponderAddsACookie() throws IOException {
//        Map<String, Object> request = new HashMap<>();
//        request.put("httpVersion", "HTTP/1.1");
//        request.put("method", "GET");
//        request.put("resource", "/img/BruslyDog.jpeg");
//        request.put("cookie", "snickerdoodle=leo");
//
//        Map<String, Object> response = new FileResponder(helper.root).respond(request);
//        assertTrue(response.containsKey("Set-Cookie"));
//        assertEquals("snickerdoodle=leo", response.get("Set-Cookie"));
//    }
//
//}
