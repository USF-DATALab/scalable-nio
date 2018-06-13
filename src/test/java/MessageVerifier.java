import MessageVerifierUtility.MVRequest;
import MessageVerifierUtility.MVResponse;
import Response.Responder;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MessageVerifier implements Responder {

    @Override
    public String readAndRespond(String message) {
        MVRequest MVRequest;

        MVRequest = new Gson().fromJson(message, MVRequest.class);
        return this.buildResponse(this.verifyMessage(MVRequest));
    }

    private boolean verifyMessage(MVRequest MVRequest) {
        try {
            return MVRequest.getChecksum().equals(TestUtility.generatedSHA512(MVRequest.getBody()));
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println("unable to verify message " + e.getLocalizedMessage());
            return false;
        }
    }

    private String buildResponse(boolean status) {
        MVResponse MVResponse;

        MVResponse = new MVResponse();
        MVResponse.setStatus(status);
        return new Gson().toJson(MVResponse);
    }
}
