package httpServerTests;

import httpServer.FileResponder;
import httpServer.Server;
import org.junit.jupiter.api.Test;
import server.ExceptionInfo;
import server.Router;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouterTest {
    @Test
    public void routesToFileResponder() throws InterruptedException, ExceptionInfo, IOException {
        Router router = new Router();
        Map<String, Object> request = new HashMap<>();
        request.put("httpVersion", "HTTP/1.1");
        request.put("method", "GET");
        request.put("resource", "/img/BruslyDog.jpeg");
        Server.registerResponders(router,  new File(".").getCanonicalPath() + "/serverFiles");

        router.route(request);
        assertTrue(router.getResponder() instanceof FileResponder);

    }
}
