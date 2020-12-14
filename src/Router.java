import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Router {
    private Responder responder;
    private final Map<String, Map<String, Responder>> methods;
    private final List<String> resourceRegexs;
    private String responderPointer;
    private String method;
    private String resource;

    public Router() {
        methods = new HashMap<>();
        resourceRegexs = new ArrayList<>();
    }

    public Response route(Request request) throws IOException {
        resource = request.get("resource");
        method = request.get("method");
        for (String resourceRegex : resourceRegexs) {
            System.out.println("resource = " + resource);
            System.out.println("resourceRegex = " + resourceRegex);
            if (Pattern.matches(resourceRegex, resource)) {
                responderPointer = resourceRegex;
                break;
            }
            else {
                method = "BAD";
                responderPointer = "bad";
            }
        }
        responder = methods.get(method).get(responderPointer);
        Response response = responder.respond(request);
        return response;
    }

    public void registerResponder(String method, String resourceRegex, Responder responder) {
        resourceRegexs.add(resourceRegex);
        if (methods.containsKey(method))
            methods.get(method).put(resourceRegex, responder);
        else {
            Map<String, Responder> responders = new HashMap<String, Responder>();
            responders.put(resourceRegex, responder);
            methods.put(method, responders);
        }
    }

    public Responder getResponder() {
        return responder;
    }
}
