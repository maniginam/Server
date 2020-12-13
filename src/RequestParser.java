import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private List<String> methods;
    private String resource;
    private String method;
    Request requestMap;
    private String version;
    private boolean isHeaderComplete;
    private String header;

    public RequestParser() {
        methods = new ArrayList<String>();
        methods.add("GET");
        isHeaderComplete = false;
    }

    public Request parse(byte[] request) throws IOException, ExceptionInfo {
        requestMap = new Request();

        while (!isHeaderComplete) {
            header = new String(request, StandardCharsets.UTF_8);
            if (header.endsWith("\r\n\r\n")) {
                isHeaderComplete = true;
                String startLine = header.split("\r\n")[0];
                splitStartLine(startLine);
            }
        }

        return requestMap;
    }

    private void splitStartLine(String startLine) throws IOException, ExceptionInfo {
        String[] startLineParts = startLine.split(" ");
        String method = startLineParts[0];
        if (startLineParts[startLineParts.length - 1].contains("HTTP/1.1")) {
            requestMap.put("httpVersion", "HTTP/1.1");
            requestMap.put("method", extractMethod(method));
            requestMap.put("resource", extractResource(startLineParts));
        } else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private String extractMethod(String method) throws ExceptionInfo, IOException {
        if (methods.contains(method))
            return method;
        else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private String extractResource(String[] startLine) throws ExceptionInfo, IOException {
        if (startLine.length < 3) {
            resource = "/index.html";
        } else if (startLine.length == 3) {
            if (startLine[1].matches("/"))
                resource = "/index.html";
            else
                resource = startLine[1];
        } else throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
        return resource;
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

    public Request getRequest() {
        return requestMap;
    }
}
