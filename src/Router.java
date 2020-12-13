public class Router {
    private Response response;

    public Response route(Request request) {
        String resource = request.get("resource");
        String method = request.get("method");

        return response;
    }
}
