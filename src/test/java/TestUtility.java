import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtility {
    private static final int MaxMessageLength = 1000;
    private static final String TestChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_+";
    private static final int TestCharsLength = TestChars.length();

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

    private static String getRandomMessage() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String randomString, checkSum;
        Message message;

        randomString = getRandomString(ThreadLocalRandom.current().nextInt(100, MaxMessageLength));
        checkSum = Utility.generatedSHA512(randomString);
        message = new Message(randomString, checkSum);
        return new Gson().toJson(message);
    }

    private static ByteBuffer getRandomMessageByteBuffer()
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return ByteBuffer.wrap(getRandomMessage().getBytes());
    }

    private static boolean messageSuccess(ByteBuffer byteBuffer) {
        String response;
        MessageVerifierResponse messageVerifierResponse;
        byte validBytes[];
        int i;

        validBytes = new byte[byteBuffer.position()];

        for (i = 0; i< validBytes.length; i++) {
            validBytes[i] = byteBuffer.get(i);
        }

        response = new String(validBytes).trim();
        messageVerifierResponse = new Gson().fromJson(response, MessageVerifierResponse.class);

        return messageVerifierResponse.getStatus();
    }

    public static boolean simpleTest(InetSocketAddress inetSocketAddress)
            throws IOException, NoSuchAlgorithmException {
        ByteBuffer byteBuffer;

        try(SocketChannel socketChannel = SocketChannel.open(inetSocketAddress)) {
            byteBuffer = TestUtility.getRandomMessageByteBuffer();
            socketChannel.write(byteBuffer);
            byteBuffer.flip();
            socketChannel.read(byteBuffer);

            return messageSuccess(byteBuffer);
        }
    }

}
