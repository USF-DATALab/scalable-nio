import Response.ResponseManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class MessageVerifierTest {
    private static final int SingleRequestTestPort = 8080;
    private static final int MultiRequestTestPort = 8090;
    private static final int SingleRequestFailureTestPort = 8080;

    private JobServer setupServer(int port) {
        ResponseManager responseManager;
        MessageVerifier messageVerifier;

        responseManager = new ResponseManager();
        messageVerifier = new MessageVerifier();
        responseManager.register(messageVerifier.getClass().getName(), messageVerifier);

        return new JobServer(port, 128, 10, responseManager);
    }

    @Test
    public void singleRequestTest() {
        InetSocketAddress inetSocketAddress;
        JobServer jobServer;

        jobServer = this.setupServer(SingleRequestTestPort);

        new Thread(new TestServer(jobServer)).start();

        try {
            Thread.sleep(20);

            inetSocketAddress = new InetSocketAddress("localhost", SingleRequestTestPort);
            Assertions.assertTrue(TestUtility.singleRequestTest(inetSocketAddress));
        }
        catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        jobServer.stopServer();
    }

    @Test
    public void singleRequestFailureTest() {
        InetSocketAddress inetSocketAddress;
        JobServer jobServer;

        jobServer = this.setupServer(SingleRequestFailureTestPort);

        new Thread(new TestServer(jobServer)).start();

        try {
            Thread.sleep(20);

            inetSocketAddress = new InetSocketAddress("localhost", SingleRequestFailureTestPort);
            Assertions.assertFalse(TestUtility.singleRequestFailureTest(inetSocketAddress));
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

        jobServer = this.setupServer(MultiRequestTestPort);
        requestCount = 5;

        new Thread(new TestServer(jobServer)).start();

        try {
            Thread.sleep(20);

            inetSocketAddress = new InetSocketAddress("localhost", MultiRequestTestPort);
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
