package httpServer;

import server.Responder;

import java.util.HashMap;
import java.util.Map;

public class FormResponder implements Responder {
    private final Map<String, Object> responseMap;
    private Map<String, Object> request;
    private byte[] body;

    public FormResponder() {
        responseMap = new HashMap<>();
        responseMap.put("statusCode", 200);
        responseMap.put("Content-Type", "text/html");
    }

    @Override
    public Map<String, Object> respond(Map<String, Object> request) {
        this.request = request;
        responseMap.put("body", addFormMsgToRspMap());
        responseMap.put("Content-Length", getBodySize());
        responseMap.put("Set-Cookie", String.valueOf(request.get("cookie")));
        return responseMap;
    }

    public byte[] addFormMsgToRspMap() {
        String[] entries = String.valueOf(request.get("resource")).split("[?=&]");
        String bodyMsg = "<h2>GET Form</h2>";
        for (int i = 1; i < entries.length; i= i+2) {
            bodyMsg = bodyMsg + "<li>" +
                    entries[i] + ": " + entries[i+1] +
                    "</li>";
        }
        body = bodyMsg.getBytes();
        return body;
    }

    private int getBodySize() {
        if (body == null)
            body = addFormMsgToRspMap();
        return body.length;
    }
}
