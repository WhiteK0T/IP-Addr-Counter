package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.ArrayBitSet;
import org.tehlab.whitek0t.dto.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;

import static org.tehlab.whitek0t.util.Util.getLongFromIpAddress_Optimized;

public class UniqueIpAddressCounter_BufferedReader implements Worker {

    private final ArrayBitSet arrayBitSet = new ArrayBitSet((long) Integer.MAX_VALUE << 1);

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;
        long uniqueAddresses = 0;
        long numberOfLines = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                long ipAddress = getLongFromIpAddress_Optimized(line);
                arrayBitSet.set(ipAddress);
                numberOfLines++;
            }
            uniqueAddresses = arrayBitSet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }
}