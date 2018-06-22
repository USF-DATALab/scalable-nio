package Response;

/**
 * Represent request data
 *
 * {
 *     message: "",
 *     responderName: "",
 * }
 *
 */
public class RequestMessage {
    public String message;
    public String responderName;

    public RequestMessage(String message, String responderName) {
        this.message = message;
        this.responderName = responderName;
    }

    /**
     * @return String - Message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return String - Responder name
     */
    public String getResponderName() { return this.responderName; }


    /**
     * Returns a brief description of this potion. Will change
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("%s:%s", this.responderName, this.getMessage());
    }
}
