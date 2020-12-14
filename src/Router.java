import com.sun.deploy.net.MessageHeader;

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
    private String resourceRegex;

    public Router() {
        methods = new HashMap<>();
        resourceRegexs = new ArrayList<>();
    }

    public Response route(Request request) throws IOException {
        String resource = request.get("resource");
        String method = request.get("method");
        for (String resourceRegex : resourceRegexs) {
            if (Pattern.matches(resourceRegex, resource))
                this.resourceRegex = resourceRegex;
        }
        responder = methods.get(method).get(resourceRegex);
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
