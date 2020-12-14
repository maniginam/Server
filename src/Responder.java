import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface Responder {


    Request request = new Request();
    HashMap<String, Object> response = new Response();
    Response respond(Request request) throws IOException;
    void setHeader() throws IOException;
    void setBody(String resource) throws IOException;

}
