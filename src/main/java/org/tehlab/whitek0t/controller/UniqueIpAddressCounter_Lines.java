package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.BitArraySet;
import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.tehlab.whitek0t.util.Util.getLongFromIpAddress_InetAddress;

public class UniqueIpAddressCounter_Lines implements Worker {

    private final BitArraySet bitArraySet = new BitArraySet((long) Integer.MAX_VALUE << 1);

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;
        long uniqueAddresses = 0;
        var lambdaContext = new Object() {
            long numberOfLines = 0;
        };

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                try {
                    long ipAddress = getLongFromIpAddress_InetAddress(line);
                    bitArraySet.set(ipAddress);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                lambdaContext.numberOfLines++;
            });

            uniqueAddresses = bitArraySet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, lambdaContext.numberOfLines, leadTime);
    }
}