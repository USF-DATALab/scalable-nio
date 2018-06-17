import MessageVerifierUtility.MVRequest;
import MessageVerifierUtility.MVResponse;
import Response.RequestMessage;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtility {
    private static final int MaxMessageLength = 1000;
    private static final String TestChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_+";
    private static final int TestCharsLength = TestChars.length();

    public static String generatedSHA512(String message) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest messageDigest;

        messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(message.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, messageDigest.digest()));
    }

    private static String getRandomString(int count) {
        StringBuilder builder;

        builder = new StringBuilder();

        while (count-- != 0) {
            int index;

            index = ThreadLocalRandom.current().nextInt(0, TestCharsLength);
            builder.append(TestChars.charAt(index));
        }

        return builder.toString();
    }

    private static String getRandomMessageDataAsJSON() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String randomString, checkSum;
        MVRequest MVRequest;

        randomString = getRandomString(ThreadLocalRandom.current().nextInt(100, MaxMessageLength));
        checkSum = generatedSHA512(randomString);
        MVRequest = new MVRequest(randomString, checkSum);

        return new Gson().toJson(MVRequest);
    }

    private static String getRandomMessageAsJSON() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String body;
        RequestMessage requestMessage;

        body = getRandomMessageDataAsJSON();

        requestMessage = new RequestMessage(body, MessageVerifier.class.getName());
        return new Gson().toJson(requestMessage);
    }

    private static ByteBuffer getRandomMessageByteBuffer()
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return ByteBuffer.wrap(getRandomMessageAsJSON().getBytes());
    }

    private static boolean messageSuccess(ByteBuffer byteBuffer) {
        String response;
        MVResponse messageMVResponse;
        byte validBytes[];
        int i;

        validBytes = new byte[byteBuffer.position()];

        for (i = 0; i< validBytes.length; i++) {
            validBytes[i] = byteBuffer.get(i);
        }

        response = new String(validBytes).trim();
        messageMVResponse = new Gson().fromJson(response, MVResponse.class);

        return messageMVResponse.getStatus();
    }

    public static boolean simpleTest(InetSocketAddress inetSocketAddress)
            throws IOException, NoSuchAlgorithmException {
        ByteBuffer byteBuffer;
        boolean status;

        try(SocketChannel socketChannel = SocketChannel.open(inetSocketAddress)) {
            byteBuffer = TestUtility.getRandomMessageByteBuffer();
            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }
            byteBuffer.flip();
            socketChannel.read(byteBuffer);

            status = messageSuccess(byteBuffer);
        }

        return status;
    }

}
