//package main.java.httpServer;
//
//import main.java.server.ExceptionInfo;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
//public class FileResponse implements Response {
//    private Map<String, Object> response;
//    private Thread thread;
//    private Map<String, Object> request;
//    private Map<String, String> types;
//    private Map<String, String> header;
//    private byte[] body;
//    private String root;
//    private String serverName;
//    private boolean hasSentResponse;
//    private boolean hasResponded;
//
//    public FileResponse(Map<String, Object> request) {
//        this.request = request;
//        hasSentResponse = false;
//        types = new HashMap<String, String>();
//        types.put("html", "text/html");
//        types.put("pdf", "application/pdf");
//        types.put("jpg", "image/jpeg");
//        types.put("jpeg", "image/jpeg");
//        types.put("png", "image/png");
//    }
//
//    @Override
//    public Map<String, Object> buildResponse() {
//        thread = new Thread(this);
//        thread.start();
//        return response;
//    }
//
//    @Override
//    public void stop() throws InterruptedException {
//        if (thread != null) {
//            thread.join();
//        }
//    }
//
//    @Override
//    public void hasGivenResponse(boolean b) {
//        hasSentResponse = b;
//    }
//
//    @Override
//    public boolean hasResponded() {
//        return hasResponded;
//    }
//
//    @Override
//    public void run() {
//            setBody();
//        String type;
//        type = determineFileType();
//        setHeader(type);
//        setResponse(200);
//        hasResponded = true;
//    }
//
//    private String determineFileType() {
//        String resource = String.valueOf(request.get("resource"));
//        String fileType = resource.split("\\.")[resource.split("\\.").length - 1];
//        return types.get(fileType);
//    }
//
//    public void setResponse(int statusCode) {
//        response.put("statusCode", statusCode);
//        response.put("headers", header);
//        response.put("body", body);
//    }
//
//    public void setBody() throws ExceptionInfo, IOException {
//        String resource = String.valueOf(request.get("resource"));
//        Path path = Paths.get((root + resource));
//        try {
//            body = Files.readAllBytes(path);
//        } catch (IOException e) {
//            // TODO: 12/15/20 This feels ugly--ask about this
//            throw new ExceptionInfo("The page you are looking for is 93 million miles away!");
//        }
//    }
//
//    public void setHeader(String type) {
//        header = new HashMap<>();
//        header.put("Server", serverName);
//        header.put("Content-Type", type);
//        header.put("Content-Length", String.valueOf(body.length));
//    }
//}
