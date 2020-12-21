package server;

import java.io.IOException;
import java.util.Map;

public interface ResponseBuilder {
    byte[] buildResponse(Map<String, Object> responseMap) throws IOException;
}
