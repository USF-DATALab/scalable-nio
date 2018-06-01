import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class SingleRequestTest {
    private static final String TestChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_+";
    private static final int TestCharsLength = TestChars.length();
    private static final int MaxMessageLength = 1000;
    private static final int TestPort = 8080;

    private String getRandomString(int count) {
        StringBuilder builder;

        builder = new StringBuilder();

        while (count-- != 0) {
            int index;

            index = ThreadLocalRandom.current().nextInt(0, TestCharsLength);
            builder.append(TestChars.charAt(index));
        }

        return builder.toString();
    }

    private String getRandomMessage() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String randomString, checkSum;
        Message message;

        randomString = this.getRandomString(ThreadLocalRandom.current().nextInt(100, MaxMessageLength));
        checkSum = Utility.generatedSHA512(randomString);
        message = new Message(randomString, checkSum);
        return new Gson().toJson(message);
    }

    private ByteBuffer getRandomMessageByteBuffer() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return ByteBuffer.wrap(this.getRandomMessage().getBytes());
    }

    private void assertResponse(ByteBuffer byteBuffer) {
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

        Assertions.assertTrue(messageVerifierResponse.getStatus());
    }

    @Test
    public void singleRequestTest() {
        SocketChannel socketChannel;
        InetSocketAddress inetSocketAddress;
        ByteBuffer byteBuffer;

        new Thread(new TestServer()).start();

        try {
            Thread.sleep(100);

            inetSocketAddress = new InetSocketAddress("localhost", TestPort);
            socketChannel = SocketChannel.open(inetSocketAddress);
            byteBuffer = this.getRandomMessageByteBuffer();
            socketChannel.write(byteBuffer);
            byteBuffer.flip();
            socketChannel.read(byteBuffer);
            this.assertResponse(byteBuffer);
        }
        catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

    }

    private class TestServer implements Runnable {

        @Override
        public void run() {
            MessageVerifier messageVerifier;

            messageVerifier = new MessageVerifier(TestPort, 128);
            messageVerifier.startServer();
        }
    }

}
