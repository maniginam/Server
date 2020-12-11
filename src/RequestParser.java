import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private List<String> methods;
    private String resource;
    private String method;
    Request requestMap;
    private String version;
    private boolean isHeaderComplete;

    public RequestParser() {
        methods = new ArrayList<String>();
        methods.add("GET");
        isHeaderComplete = false;
    }

    public Request parse(String request) throws IOException, ExceptionInfo {
        requestMap = new Request();
        String[] startLine = request.split(" ");
        parseStartLine(startLine);
        requestMap.put("method", method);
        requestMap.put("resource", resource);
        requestMap.put("httpVersion", version);

    return requestMap;
    }

    private void parseStartLine(String[] startLine) throws IOException, ExceptionInfo {
        String maybeMethod = startLine[0];
        if(startLine[startLine.length - 1].matches("HTTP/1.1")) {
            version = "HTTP/1.1";
            method = parseMethod(maybeMethod);
            resource = parseResource(startLine);
        } else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private String parseResource(String[] startLine) throws ExceptionInfo, IOException {
        if (startLine.length < 3) {
            return "/index.html";
        } else if (startLine.length == 3) {
            if (startLine[1].matches("/"))
                return "/index.html";
            else
                return startLine[1];
        } else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private String parseMethod(String method) throws ExceptionInfo, IOException {
        if (methods.contains(method))
            return method;
        else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public boolean isHeaderComplete() {
        return isHeaderComplete;
    }
}
