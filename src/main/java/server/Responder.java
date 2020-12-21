package server;

import java.io.IOException;
import java.util.Map;

public interface Responder {
    byte[] respond(Map<String, Object> request, ResponseBuilder builder) throws IOException, ExceptionInfo, InterruptedException;
}