
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MessageVerifier {
    private static final int Port = 8000;
    private static final int MinBuffer = 64;

    public static void main(String args[]) {
        MessageVerifier messageVerifier = new MessageVerifier(Port, MinBuffer);
        messageVerifier.startServer();
    }

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int port;
    private int minBuffer;

    private MessageVerifier(int port, int minBuffer) {
        this.port = port;
        this.minBuffer = minBuffer;
    }

    private void startServer() {
        InetSocketAddress inetSocketAddress;

        try {
            inetSocketAddress = new InetSocketAddress("localhost", this.port);

            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(inetSocketAddress);
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

            while (true) {
                this.selector.select();
                Set<SelectionKey> selectedKeys;
                Iterator<SelectionKey> iter;

                selectedKeys = this.selector.selectedKeys();
                iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey selectionKey = iter.next();

                    if (selectionKey.isAcceptable()) {
                        this.registerClient();
                    }

                    else if (selectionKey.isReadable()) {
                        this.readAndRespond(selectionKey);
                    }

                    iter.remove();
                }
            }
        }
        catch (IOException e) {
            // Do Something
        }
    }

    private void registerClient(){
        SocketChannel socketChannel;

        try {
            socketChannel = this.serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(this.selector, SelectionKey.OP_READ);
        }
        catch (IOException e) {
            // Do Something
        }
    }

    private void readAndRespond(SelectionKey selectionKey) {
        SocketChannel socketChannel;
        ArrayList<ByteBuffer> buffers;
        String data;
        HashMap<String, String> responseData;

        socketChannel = (SocketChannel) selectionKey.channel();

        try {
            buffers = this.readRequest(socketChannel);
            data = this.extractData(buffers);
            responseData = new Gson().fromJson(data, HashMap.class);
        }
        catch (IOException e) {
            // Do Something
        }

    }

    private ArrayList<ByteBuffer> readRequest(SocketChannel socketChannel) throws IOException {
        int counter;
        ByteBuffer current;
        ArrayList<ByteBuffer> buffers;

        counter = 2;
        buffers = new ArrayList<>();
        current = ByteBuffer.allocate(this.minBuffer);
        buffers.add(current);

        while (socketChannel.read(current) > 0) {
            if (!current.hasRemaining()) {
                current = ByteBuffer.allocate(this.minBuffer * 2 * counter);
                buffers.add(current);
                counter++;
            }
        }

        return buffers;
    }

    private String extractData(ArrayList<ByteBuffer> buffers) {
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();
        for(ByteBuffer buffer : buffers) {
            for(Byte bufferByte : buffer.array()) {
                stringBuilder.append(bufferByte);
            }
        }

        return stringBuilder.toString();
    }




}
