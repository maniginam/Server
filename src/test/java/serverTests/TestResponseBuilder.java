package test.java.serverTests;

import main.java.server.ResponseBuilder;

import java.io.IOException;
import java.util.Map;

public class TestResponseBuilder implements ResponseBuilder {
    @Override
    public byte[] buildResponse(Map<String, Object> responseMap) throws IOException {
        return new byte[0];
    }
}
