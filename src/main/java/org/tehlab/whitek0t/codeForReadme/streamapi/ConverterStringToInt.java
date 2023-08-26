package org.tehlab.whitek0t.codeForReadme.streamapi;

import java.util.function.ToIntFunction;

public class ConverterStringToInt implements ToIntFunction<String> {

    @Override
    public int applyAsInt(String ipAddress) {
        var octets = ipAddress.split("\\.");
        long result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return (int) result;
    }
}