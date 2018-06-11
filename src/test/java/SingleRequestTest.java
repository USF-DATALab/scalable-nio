import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class SingleRequestTest {
    private static final int TestPort = 8080;

    @Test
    public void singleRequestTest() {
        InetSocketAddress inetSocketAddress;

        new Thread(new TestServer()).start();

        try {
            Thread.sleep(100);

            inetSocketAddress = new InetSocketAddress("localhost", TestPort);
            Assertions.assertTrue(TestUtility.simpleTest(inetSocketAddress));
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
