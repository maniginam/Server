import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouterTest {

    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
    }

    @Test
    public void registersFileResponder() throws IOException {
        Router router = new Router();
        router.registerResponder("GET", ".*", new FileResponder(helper.root));
        Request request = new Request();
        request.put("method", "GET");
        request.put("resource", "/index.html");

        router.route(request);
        Responder result = router.getResponder();

        assertTrue(result instanceof FileResponder);
    }
}
