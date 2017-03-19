package server;

import network.DNSProvider;
import storage.IPDomainStorage;
import utils.IPValidator;

import java.net.DatagramPacket;
import java.util.Arrays;

public class PacketHandler implements PacketReceiver.Callback
{
    private static final int DOMAIN_START_INDEX = 12;
    private static final int IP_START_INDEX = 44;
    private static final int IP_END_INDEX = 47;

    @Override
    public void onReceive(DatagramPacket data)
    {
        String domain = getDomainName(data.getData());
        String ip = IPDomainStorage.getBoundValue(domain);

        if (!ip.equals(IPDomainStorage.NOT_FOUND))
        {
            System.out.println("FROM CACHE");
            DNSProvider.sendResponsePacket(data, domain, ip);
        }
        else
        {
            System.out.println("FROM GOOGLE");
            DNSProvider.domainRecognitionRequest(domain, (packet) ->
            {
                System.out.println(Arrays.toString(packet));
                String address = getIPFromDNSPacket(packet);

                if (IPValidator.validate(address))
                {
                    IPDomainStorage.putEntry(domain, address, (int)(System.currentTimeMillis() / 1000) + 20);
                    DNSProvider.sendResponsePacket(data, domain, address);
                }
            });
        }
    }

    private String getDomainName(byte[] data)
    {
        StringBuilder stringBuffer = new StringBuilder();

        int index = DOMAIN_START_INDEX;
        int count = data[index];

        while (count >= 0)
        {
            for (int i = 1; i <= count; i++)
                stringBuffer.append((char) data[index + i]);
            index = index + count + 1;
            count = data[index];

            if (count == 0)
                break;

            stringBuffer.append(".");
        }

        return stringBuffer.toString();
    }

    private String getIPFromDNSPacket(byte[] packet)
    {
        StringBuilder ipBuilder = new StringBuilder();
        for (int i = IP_START_INDEX; i <= IP_END_INDEX; i++) {
            ipBuilder.append(256 + packet[i]);
            if (i == IP_END_INDEX) break;
            ipBuilder.append(".");
        }

        return ipBuilder.toString();
    }
}
