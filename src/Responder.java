import java.io.IOException;

public interface Responder {

    Response respond(Request requestMap) throws IOException, ExceptionInfo;

    void setHeader() throws IOException;

    void setBody() throws IOException, ExceptionInfo;

    void setResponse();
}
