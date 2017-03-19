package network;

import server.DNSServer;
import server.PacketReceiver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class DNSProvider
{
    private static final String DNS_SERVER_ADDRESS = "8.8.8.8";

    public static void domainRecognitionRequest(String domain, Callback callback)
    {
        try
        {
            byte[] packet = getDNSPacket(domain);
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket dnsReqPacket = new DatagramPacket(
                    packet, packet.length, InetAddress.getByName(DNS_SERVER_ADDRESS), PacketReceiver.PORT);
            socket.send(dnsReqPacket);

            byte[] buf = new byte[1024];
            DatagramPacket response = new DatagramPacket(buf, buf.length);
            socket.receive(response);

            byte[] answer = response.getData();

            callback.onResponse(answer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendResponsePacket(DatagramPacket packet, String domain, String ip)
    {
        byte[] data = packet.getData();

        PacketReceiver packetReceiver = DNSServer.getPacketReceiver();
        DatagramSocket serverSocket = packetReceiver.getServerSocket();
        byte[] response = buildDNSAnswerPacket(data, domain, ip);

        try {
            DatagramPacket sendPacket =
                    new DatagramPacket(response, response.length);
            serverSocket.send(sendPacket);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        System.out.println(domain + " : " + ip);
    }

    private static byte[] getDNSPacket(String domain) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(0x1234);
        dos.writeShort(0x0100);
        dos.writeShort(0x0001);
        dos.writeShort(0x0000);
        dos.writeShort(0x0000);
        dos.writeShort(0x0000);

        String[] domainParts = domain.split("\\.");

        for (String domainPart : domainParts) {
            byte[] domainBytes = domainPart.getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }

        dos.writeByte(0x00);
        dos.writeShort(0x0001);
        dos.writeShort(0x0001);

        return baos.toByteArray();
    }

    private static byte[] buildDNSAnswerPacket(byte[] data, String domain, String ip)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try
        {
            dos.writeByte(data[0]); // transaction id
            dos.writeByte(data[1]); // transaction id
            dos.writeShort(0x8180); // flags
            dos.writeShort(0x0001); // questions count
            dos.writeShort(0x0001); // answer

            dos.writeShort(0x0000);
            dos.writeShort(0x0000);

            for (int i = 0; i < 2; i++) {
                String[] arr = domain.split("\\."); // write domain
                for (String s: arr)
                {
                    dos.writeByte(s.length());
                    dos.writeBytes(s);
                }

                dos.writeByte(0);

                dos.writeShort(0x0001); // host address
                dos.writeShort(0x0001);
            }

            dos.writeShort(0x0000); // TTL
            dos.writeShort(0x0000);
            dos.writeShort(0x0004); // Data length

            for (String s : ip.split("\\."))
                dos.writeByte(Integer.parseInt(s));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public interface Callback
    {
        void onResponse(byte[] packet);
    }
}
