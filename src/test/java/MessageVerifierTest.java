import Response.ResponseManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class MessageVerifierTest {
    private static final int TestPort = 8080;

    private JobServer setupServer() {
        ResponseManager responseManager;
        MessageVerifier messageVerifier;

        responseManager = new ResponseManager();
        messageVerifier = new MessageVerifier();
        responseManager.register(messageVerifier.getClass().getName(), messageVerifier);

        return new JobServer(TestPort, 128, 10, responseManager);
    }

    @Test
    public void singleRequestTest() {
        InetSocketAddress inetSocketAddress;
        JobServer jobServer;

        jobServer = this.setupServer();

        new Thread(new TestServer(jobServer)).start();

        try {
            Thread.sleep(20);

            inetSocketAddress = new InetSocketAddress("localhost", TestPort);
            Assertions.assertTrue(TestUtility.simpleTest(inetSocketAddress));
        }
        catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        jobServer.stopServer();
    }

    @Test
    public void multipleRequestTest() {
        InetSocketAddress inetSocketAddress;
        JobServer jobServer;
        int requestCount;

        jobServer = this.setupServer();
        requestCount = 5;

        new Thread(new TestServer(jobServer)).start();

        try {
            Thread.sleep(20);

            inetSocketAddress = new InetSocketAddress("localhost", TestPort);
            Assertions.assertEquals(requestCount, TestUtility.nRequestTest(inetSocketAddress, requestCount));
        }
        catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        jobServer.stopServer();
    }

    private class TestServer implements Runnable {
        private JobServer jobServer;

        private TestServer(JobServer jobServer) {
            this.jobServer = jobServer;
        }

        @Override
        public void run() {
            jobServer.startServer();
        }
    }

}
