package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.BitArraySet;
import org.tehlab.whitek0t.dto.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;

import static org.tehlab.whitek0t.util.Util.getLongFromIpAddress_InetAddress;

public class UniqueIpAddressCounter_BufferedReader implements Worker {

    private final BitArraySet bitArraySet = new BitArraySet((long) Integer.MAX_VALUE << 1);

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