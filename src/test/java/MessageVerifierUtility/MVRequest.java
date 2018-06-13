package MessageVerifierUtility;

public class MVRequest {
    public String body;
    public String checksum;

    public MVRequest(String body, String checksum) {
        this.body = body;
        this.checksum = checksum;
    }

    public String getBody() {
        return this.body;
    }

    public String getChecksum() {
        return this.checksum;
    }
}
