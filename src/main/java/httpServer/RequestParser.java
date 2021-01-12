package httpServer;

import server.ExceptionInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private final List<String> methods;
    private final BufferedInputStream inputStream;
    Map<String, Object> requestMap;
    private boolean isHeaderComplete;
    private int contentLength;
    private String header;
    private String multiPartHeader;

    public RequestParser(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
        methods = new ArrayList<>();
        methods.add("GET");
        methods.add("POST");
        isHeaderComplete = false;
        requestMap = new HashMap<>();
    }

    public Map<String, Object> parse() throws IOException, ExceptionInfo {
        extractHeader();
        splitHeadersToMap(header);
        setContentLength();
        if (isRequestMultiPart())
            parseMultiPart();
        return requestMap;
    }

    private void extractHeader() throws IOException {
        ByteArrayOutputStream headerOutput = new ByteArrayOutputStream();
        while (!isHeaderComplete) {
            headerOutput.write(inputStream.read());
            header = new String(headerOutput.toByteArray(), StandardCharsets.UTF_8);
            if (header.endsWith("\r\n\r\n")) {
                isHeaderComplete = true;
            }
        }
    }

    private void splitHeadersToMap(String header) throws IOException, ExceptionInfo {
        String[] headerLines = header.split("\r\n");
        for (String line : headerLines) {
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

    private void setContentLength() {
        if (requestMap.containsKey("Content-Length"))
            contentLength = Integer.parseInt(String.valueOf(requestMap.get("Content-Length")));
        else contentLength = 0;
    }

    private boolean isRequestMultiPart() {
        return contentLength > 0;
    }

    public void parseMultiPart() throws IOException, ExceptionInfo {
        byte[] multiPartBytes = readRestOfInputStream();
        multiPartHeader = extractMultiPartHeader(multiPartBytes);
        splitHeadersToMap(multiPartHeader);
        parseRequestBody(multiPartBytes);
    }

        private byte[] readRestOfInputStream() throws IOException {
            byte[] body = new byte[contentLength];
            for (int i = 0; i < contentLength; i++) {
                body[i] = (byte) inputStream.read();
            }
            return body;
        }

        private String extractMultiPartHeader(byte[] multiPartBytes) {
            String multiPartHeader = "";
            ByteArrayOutputStream multiPartHeaderBytes = new ByteArrayOutputStream();
            for (byte multiPartByte : multiPartBytes) {
                multiPartHeaderBytes.write(multiPartByte);
                multiPartHeader = new String(multiPartHeaderBytes.toByteArray(), StandardCharsets.UTF_8);
                if (multiPartHeader.contains("\r\n\r\n")) {
                    break;
                }
            }
            return multiPartHeader;
        }

        public void parseRequestBody(byte[] multiPartBytes) {
            int fileSize = determineFileSize(multiPartBytes);
            byte[] body = new byte[fileSize];
            int headerSize = multiPartBytes.length - fileSize;
            System.arraycopy(multiPartBytes, headerSize, body, 0, fileSize);
            requestMap.put("fileSize", fileSize);
            requestMap.put("body", body);
        }

            private int determineFileSize(byte[] multiPartBytes) {
                return multiPartBytes.length
                        - multiPartHeader.length()
                        - ("\r\n--" + requestMap.get("boundary") + "--\r\n").length();
            }
}
