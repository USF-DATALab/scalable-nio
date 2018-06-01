public class Message {
    public String message;
    public String checkSum;

    public Message(String message, String checkSum) {
        this.message = message;
        this.checkSum = checkSum;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCheckSum() {
        return this.checkSum;
    }
}
