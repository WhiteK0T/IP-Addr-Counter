package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.BitArraySet;
import org.tehlab.whitek0t.dto.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;

public class UniqueIpAddressCounter_BufferedReader implements Worker {

    private final BitArraySet bitArraySet = new BitArraySet((long) Integer.MAX_VALUE << 1);

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

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;
        long uniqueAddresses = 0;
        long numberOfLines = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                long ipAddress = getLongFromIpAddress_InetAddress(line);
                bitArraySet.set(ipAddress);
                numberOfLines++;
            }
            uniqueAddresses = bitArraySet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }
}