package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {
    private Responder responder;
    private final Map<String, HashMap<Pattern, Responder>> methods;
    private final List<Pattern> resourceRegexs;
    private Pattern responderPointer;
    private String method;
    private String resource;
    private byte[] response;
    private Map<String, Object> request;
    private List<Responder> responders;

    public Router() {
        methods = new HashMap<String, HashMap<Pattern, Responder>>();
        resourceRegexs = new ArrayList<Pattern>();
        responders = new ArrayList<>();
    }

    public byte[] route(Map<String, Object> request, ResponseBuilder builder) throws IOException, ExceptionInfo, InterruptedException {
        this.request = request;
        if (request != null) {
            method = String.valueOf(request.get("method"));
            resource = String.valueOf(request.get("resource"));
            responderPointer = null;
            for (Pattern resourceRegex : resourceRegexs) {
                Matcher m = resourceRegex.matcher(resource);
                if (m.find()) {
                    responder = methods.get(method).get(resourceRegex);
                    break;
                }
            }
            if (responder != null)
                response = responder.respond(request, builder);
            else throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        } else throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        return response;
    }

        public void registerResponder (String method, Pattern resourceRegex, Responder responder){
            responders.add(responder);
            resourceRegexs.add(resourceRegex);
            if (methods.containsKey(method))
                methods.get(method).put(resourceRegex, responder);
            else {
                HashMap<Pattern, Responder> responders = new HashMap<>();
                responders.put(resourceRegex, responder);
                methods.put(method, responders);
            }
        }

        public Responder getResponder () {
            return responder;
        }
    }