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

    public Response route(Request request) throws IOException, ExceptionInfo {
        if (request != null) {
            method = String.valueOf(request.get("method"));
            resource = String.valueOf(request.get("resource"));
            String target = resource;
            if (resource.contains("\r\n"))
                target = resource.split("\r\n")[0];
            responderPointer = null;
            for (String resourceRegex : resourceRegexs) {
                if (Pattern.matches(resourceRegex, target)) {
                    responderPointer = resourceRegex;
                }
            }

            if (responderPointer != null && methods.containsKey(method)) {
                responder = methods.get(method).get(responderPointer);
                Response response = responder.respond(request);
                return response;
            } else throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        } else throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
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
