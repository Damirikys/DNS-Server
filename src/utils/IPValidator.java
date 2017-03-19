package utils;

import java.util.regex.Pattern;

public abstract class IPValidator
{
    private static final Pattern IPADDRESS_PATTERN =
            Pattern.compile(
                    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
            );

    public static boolean validate(String ip)
    {
        return IPADDRESS_PATTERN.matcher(ip).find();
    }
}
