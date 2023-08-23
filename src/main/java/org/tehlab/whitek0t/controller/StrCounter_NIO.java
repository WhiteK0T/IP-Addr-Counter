package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

public class StrCounter_NIO implements Worker{
    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(512_000_000); // Размер буфера
        long numberOfLines = 0;
        LocalTime startTime = LocalTime.now();
        LocalTime leadTime = LocalTime.MIN;

        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            long fileSize = fileChannel.size();
            long bytesRead = 0;

            while (bytesRead < fileSize) {
                buffer.clear();
                bytesRead += fileChannel.read(buffer);
                buffer.flip();

                int symbol;
                while (buffer.hasRemaining()) {
                    symbol = buffer.get();
                    if (symbol == 10) {
                        numberOfLines++;
                    }
                }
                consumer.accept(bytesRead);
            }
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(0, numberOfLines, leadTime);
    }
}