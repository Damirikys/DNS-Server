package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class PacketReceiver extends Thread
{
    public static final int PORT = 53;

    Callback callback;
    private byte[] receiveData = new byte[1024];
    private DatagramSocket udpListeningSocket;

    public PacketReceiver(Callback callback) throws SocketException {
        this.callback = callback;
        udpListeningSocket = new DatagramSocket(PORT);
    }

    @Override
    public void run() {
        while (true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                udpListeningSocket.receive(receivePacket);
                callback.onReceive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DatagramSocket getServerSocket()
    {
        return udpListeningSocket;
    }

    public interface Callback {
        void onReceive(DatagramPacket packet);
    }
}
