import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private final List<String> methods;
    Request requestMap = new Request();
    private boolean isHeaderComplete;
    private int contentLength;
    private boolean isRequestComplete;
    private String boundary;
    private boolean statusLineSet;
    private boolean isMultiPartRequest;
    private boolean doneParsing;
    private int remainingBytes;
    private boolean partHeaderDone;
    private int partHeaderSize;
    private int headerSize;

    public RequestParser() {
        methods = new ArrayList<>();
        methods.add("GET");
        methods.add("POST");
        isHeaderComplete = false;
        isRequestComplete = false;
        statusLineSet = false;
        isMultiPartRequest = false;
        doneParsing = false;
    }

    public Request parse(byte[] request) throws IOException, ExceptionInfo {
        requestMap = new Request();
        if (!isHeaderComplete) {
            extractHeader(request);
        }
        return requestMap;
    }


    private void extractHeader(byte[] request) throws IOException, ExceptionInfo {
        String header = new String(request, StandardCharsets.UTF_8);
        if (header.endsWith("\r\n\r\n")) {
            isHeaderComplete = true;
            headerSize = header.length();
            String[] headers = header.split("\r\n");
            for (String line : headers) {
                splitHeaders(line);
            }
            if (requestMap.containsKey("Content-Length"))
                contentLength = Integer.parseInt(String.valueOf(requestMap.get("Content-Length")));
            if (contentLength > 0) {
                isMultiPartRequest = true;
            } else {
                doneParsing = true;
            }
        }
    }

    public Request parseParts(byte[] multiPartBytes) throws IOException, ExceptionInfo {
        String header = new String(multiPartBytes, StandardCharsets.UTF_8);
        if (header.contains("\r\n\r\n")) {
            partHeaderSize = header.length();
            // TODO: 12/17/20 ASK ABOUT THIS LENGTH!!!
            int fileSize = contentLength - partHeaderSize - String.valueOf(requestMap.get("boundary")).length() - 8;
            requestMap.put("fileSize", fileSize);
            partHeaderDone = true;
            doneParsing = true;
            String[] headers = header.split("\r\n");
            for (String line : headers)
                splitHeaders(line);
        }
        return requestMap;
    }

    private void splitHeaders(String line) throws IOException, ExceptionInfo {
        if (line.contains("HTTP/1.1")) {
            splitStartLine(line);
        } else if (line.split(": ").length > 1) {
            String[] entity = line.split(": ");
            if (line.contains("Content-Type")) {
                if (entity[1].contains("boundary=")) {
                    String[] contentType = entity[1].split("; boundary=");
                    requestMap.put("boundary", contentType[1]);
                }
            } else if (line.contains("Content-Disposition")) {
                String name = line.split("; ")[2].split("=")[1];
                requestMap.put("fileName", name);
            } else requestMap.put(entity[0], entity[1]);
        }
    }


    private void splitStartLine(String startLine) throws IOException, ExceptionInfo {
        String[] startLineParts = startLine.split(" ");
        String method = startLineParts[0];
        if (startLineParts[startLineParts.length - 1].matches("HTTP/1.1")) {
            requestMap.put("httpVersion", "HTTP/1.1");
            requestMap.put("method", extractMethod(method));
            requestMap.put("resource", extractResource(startLineParts));
        } else throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
    }

    private String extractMethod(String method) throws ExceptionInfo, IOException {
        if (methods.contains(method))
            return method;
        else {
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!  And the method " + method + " you requested is not valid!");
        }
    }

    private String extractResource(String[] startLine) throws ExceptionInfo, IOException {
        String resource = "/index.html";
        if (startLine.length == 3) {
            if (!startLine[1].matches("/"))
                resource = startLine[1];
        } else if (startLine.length != 2)
            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
        return resource;
    }

    public boolean isHeaderComplete() {
        return isHeaderComplete;
    }

    public boolean isRequestComplete() {
        return isRequestComplete;
    }

    public boolean getIsMultiPartRequest() {
        return isMultiPartRequest;
    }

    public boolean doneParsing() {
        return doneParsing;
    }

    public Request addBody(byte[] body) {
        requestMap.put("body", body);
        doneParsing = true;
        return requestMap;
    }

    public boolean getPartHeaderDone() {
        return partHeaderDone;
    }

    public int getPartHeaderSize() {
        return partHeaderSize;
    }
}
