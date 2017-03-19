package server;

import storage.IPDomainStorage;
import java.net.SocketException;
import java.sql.SQLException;

public class DNSServer
{
    private static PacketReceiver packetReceiver;

    public static void main(String[] args) throws SocketException
    {
        try
        {
            IPDomainStorage.initialize();
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            packetReceiver = new PacketReceiver(new PacketHandler());
            packetReceiver.start();
        }
    }

    public static PacketReceiver getPacketReceiver()
    {
        return packetReceiver;
    }
}
