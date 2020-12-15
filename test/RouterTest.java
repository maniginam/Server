import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouterTest {

    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper(1003);
    }

    @Test
    public void registersFileResponder() throws IOException, ExceptionInfo {
        Router router = new Router();
        router.registerResponder("GET", "([\\/\\w\\.])+(.html)$", new FileResponder("Rex's Server", helper.root));
        Request request = new Request();
        request.put("method", "GET");
        request.put("resource", "/index.html");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof FileResponder);
    }

    @Test
    public void registersListingResponder() throws IOException, ExceptionInfo {
        Router router = new Router();
        router.registerResponder("GET", "/listing", new ListingResponder("Leo's Server", helper.root));
        Request request = new Request();
        request.put("method", "GET");
        request.put("resource", "/listing");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof ListingResponder);
    }


//    @Test
//    public void registerGarbageResponder() throws IOException, ExceptionInfo {
//        Router router = new Router();
//        router.registerResponder("BAD", "bad", new ExceptionInfoResponder());
//        Request request = new Request();
//        request.put("method", "BAD");
//        request.put("resource", "leo");
//
//        router.route(request);
//        Responder result = router.getResponder();
//
//        assertTrue(result instanceof ExceptionInfoResponder);
//    }
}
