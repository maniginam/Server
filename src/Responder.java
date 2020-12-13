import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface Responder {
    Request request = new Request();
    Response response = new Response();
    int status = 200;
    Response respond(Request request) throws IOException;
    void setHeader() throws IOException;
    Map<String, String> header = new HashMap<>();
    void setBody() throws IOException;

}
