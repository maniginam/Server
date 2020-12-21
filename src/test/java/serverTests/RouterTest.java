package serverTests;

import httpServer.FileResponder;
import httpServer.ListingResponder;
import server.ExceptionInfo;
import server.Responder;
import server.Router;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouterTest {

    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper(1003);
    }

    @Test
    public void registersFileResponder() throws IOException, ExceptionInfo, InterruptedException {
        Router router = new Router();
        router.registerResponder("GET", "([\\/\\w\\.])+(.html)$", new FileResponder("Rex's http.Server", helper.root));
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("method", "GET");
        request.put("resource", "/index.html");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof FileResponder);
    }

    @Test
    public void registersListingResponder() throws IOException, ExceptionInfo, InterruptedException {
        Router router = new Router();
        router.registerResponder("GET", "/listing", new ListingResponder("Leo's http.Server", helper.root));
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("method", "GET");
        request.put("resource", "/listing");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof ListingResponder);
    }


//    @Test
//    public void registerGarbageResponder() throws IOException, server.ExceptionInfo {
//        server.Router router = new server.Router();
//        router.registerResponder("BAD", "bad", new http.ExceptionInfoResponder());
//        server.Request request = new server.Request();
//        request.put("method", "BAD");
//        request.put("resource", "leo");
//
//        router.route(request);
//        server.Responder result = router.getResponder();
//
//        assertTrue(result instanceof http.ExceptionInfoResponder);
//    }
}
