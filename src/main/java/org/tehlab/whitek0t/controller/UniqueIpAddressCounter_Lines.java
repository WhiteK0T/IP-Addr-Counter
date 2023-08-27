package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.codeForReadme.BitSetArray;
import org.tehlab.whitek0t.codeForReadme.IntContainer;
import org.tehlab.whitek0t.codeForReadme.streamapi.OptimizedConverter;
import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UniqueIpAddressCounter_Lines implements Worker {

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;
        long uniqueAddresses = 0;
        long numberOfLines = 0;

        OptimizedConverter optimizedConverter = new OptimizedConverter();
        try (Stream<String> lines = Files.lines(filePath)) {
            BitSetArray bitSetArray = lines
                    .mapToInt(optimizedConverter)
                    .collect(BitSetArray::new, IntContainer::set, IntContainer::addAll);

            uniqueAddresses = bitSetArray.countUnique();
            numberOfLines = bitSetArray.getCount();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }
}