package Response;

public class Message {
    public String message;
    public String responderName;

    public Message(String message, String responderName) {
        this.message = message;
        this.responderName = responderName;
    }

    public String getMessage() {
        return this.message;
    }

    public String getResponderName() { return this.responderName; }
}
