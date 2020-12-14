import java.io.IOException;

public class ExceptionInfo extends Throwable {
    private String message;

    public ExceptionInfo (String message) throws IOException {
        super(message);
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) throws IOException {
        this.message = message;

    }

}
