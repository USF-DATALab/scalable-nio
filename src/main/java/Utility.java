import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    public static String generatedSHA512(String message) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest messageDigest;

        messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(message.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, messageDigest.digest()));
    }

}
