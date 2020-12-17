import java.io.IOException;

public interface Responder {

    Response respond(Request requestMap) throws IOException, ExceptionInfo;

    void setHeader(String type) throws IOException, ExceptionInfo;

    void setBody() throws IOException, ExceptionInfo;

    void setResponse(int statusCode);
}