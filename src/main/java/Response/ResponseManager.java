package Response;

import com.google.gson.Gson;

import java.util.HashMap;

public class ResponseManager {
    private HashMap<String, Responder> responderHashMap;

    public ResponseManager() {
        this.responderHashMap = new HashMap<>();
    }

    public void register(String key, Responder responder) {
        this.responderHashMap.put(key, responder);
    }

    public String reply(String data) {
        RequestMessage requestMessage;

        requestMessage = new Gson().fromJson(data, RequestMessage.class);

        String responderName;
        String responderReply;

        responderName = requestMessage.getResponderName();
        responderReply = null;

        if (this.responderHashMap.containsKey(responderName)) {
            Responder responder;

            responder = this.responderHashMap.get(responderName);
            responderReply = responder.readAndRespond(requestMessage.getMessage());
        }

        return responderReply;
    }

}
