import Response.ResponseManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class MessageVerifierTest {
    private static final int TestPort = 8080;

    @Test
    public void singleRequestTest() {
        InetSocketAddress inetSocketAddress;

        new Thread(new TestServer()).start();

        try {
            Thread.sleep(20);

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
            JobServer jobServer;
            ResponseManager responseManager;
            MessageVerifier messageVerifier;

            responseManager = new ResponseManager();
            messageVerifier = new MessageVerifier();
            responseManager.register(messageVerifier.getClass().getName(), messageVerifier);

            jobServer = new JobServer(TestPort, 128, responseManager);
            jobServer.startServer();
        }
    }

}
