package server;

import java.io.IOException;
import java.util.Map;

public interface Responder {
    Map<String, Object> respond(Map<String, Object> request) throws IOException, ExceptionInfo, InterruptedException;
}