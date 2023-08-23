package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StrCounter_IO implements Worker{
    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        long numberOfLines = 0;
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;

        try (Stream<String> lines = Files.lines(filePath)) {
            numberOfLines = lines.count();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(0, numberOfLines, leadTime);
    }
}