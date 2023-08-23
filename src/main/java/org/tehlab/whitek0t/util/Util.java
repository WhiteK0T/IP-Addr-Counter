package org.tehlab.whitek0t.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Util {

    private Util() {
    }

    public static String sizeFormat(Long size) {
        if (size / 1_073_741_824 > 0) return Math.round((0.0 + size) / 1_073_741_824) + "Gb";
        if (size / 10_485_760 > 0) return Math.round((0.0 + size) / 1_048_576) + "Mb";
        if (size / 1_048_576 > 0) return Math.round((0.0 + size) / 1024) + "Kb";
        return size + "b";
    }

    public static String getStringFromLongIpAddress(long ipAddress) {
        return String.format("%d.%d.%d.%d",
                (ipAddress >> 24 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress & 0xff));
    }

    public static long getLongFromIpAddress_InetAddress(String ipAddress) throws UnknownHostException {
        long result = 0;
        for (byte b : InetAddress.getByName(ipAddress).getAddress()) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    public static long getLongFromIpAddress_parseInt(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return result;
    }
}
