package Response;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Gets reply from expected responder for request
 */
public class ResponseManager {
    private HashMap<String, Responder> responderHashMap;

    public ResponseManager() {
        this.responderHashMap = new HashMap<>();
    }

    /**
     * Registers new responder
     *
     * @param key - String - Name
     * @param responder - Responder object
     */
    public void register(String key, Responder responder) {
        this.responderHashMap.put(key, responder);
    }

    /**
     * Gets reply from expected responder
     *
     * @param data - String - Request
     * @return String reply
     */
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
