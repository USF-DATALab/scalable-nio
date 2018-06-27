package Response;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Gets reply from expected responder for request
 */
public class ResponseManager {
    private HashMap<String, Responder> responderHashMap;
    private Logger logger;

    public ResponseManager() {
        this.responderHashMap = new HashMap<>();
    }

    public void attachLogger(Logger logger) {
        this.logger = logger;
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

        if (responderName != null && this.responderHashMap.containsKey(responderName)) {
            Responder responder;

            responder = this.responderHashMap.get(responderName);
            responderReply = responder.readAndRespond(requestMessage.getMessage());
        }
        else {
            this.logger.info(String.format("Responder %s missing or not configured", responderName));
        }

        return responderReply;
    }

}
