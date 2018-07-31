import Response.ResponseManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class MessageVerifierTest {
    private static final int PORT;
    private static final InetSocketAddress INET_SOCKET_ADDRESS;
    private static JobServer JOB_SERVER;

    static {
        PORT = 8080;
        INET_SOCKET_ADDRESS = new InetSocketAddress("localhost", PORT);
    }

    @Test
    public void singleRequestTest() {
        try {
            Assertions.assertTrue(TestUtility.singleRequestTest(INET_SOCKET_ADDRESS));
        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void singleRequestFailureTest() {
        try {
            Assertions.assertFalse(TestUtility.singleRequestFailureTest(INET_SOCKET_ADDRESS));
        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void multipleRequestTest() {
        int requestCount;

        requestCount = 5;

        try {
            Assertions.assertEquals(requestCount, TestUtility.nRequestTest(INET_SOCKET_ADDRESS, requestCount));
        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    private static class TestServer implements Runnable {
        private JobServer jobServer;

        private TestServer(JobServer jobServer) {
            this.jobServer = jobServer;
        }

        @Override
        public void run() {
            jobServer.startServer();
        }
    }

    private static JobServer setupServer(int port) {
        ResponseManager responseManager;
        MessageVerifier messageVerifier;

        responseManager = new ResponseManager();
        messageVerifier = new MessageVerifier();
        responseManager.register(messageVerifier.getClass().getName(), messageVerifier);

        return new JobServer(port, 128, 10, responseManager);
    }

    @BeforeAll
    public static void setup() {
        JOB_SERVER = setupServer(PORT);
        new Thread(new TestServer(JOB_SERVER)).start();

        try {
            Thread.sleep(20);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @AfterAll
    public static void tearDown() {
        JOB_SERVER.stopServer();
    }

}

