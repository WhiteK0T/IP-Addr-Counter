package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.BitArraySet;
import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Consumer;

public class UniqueIpAddressCounter_NIO implements Worker {

    private static final BitArraySet bitArraySet = new BitArraySet((long) Integer.MAX_VALUE << 1);

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        long uniqueAddresses = 0;
        LocalTime leadTime = LocalTime.MIN;
        long numberOfLines = 0;

        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            long fileSize = fileChannel.size();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1 << 28); // Размер буфера
            LocalTime startTime = LocalTime.now();
            long bytesRead = 0;
            int baseNum = 0;
            int partNum = 0;

            while (bytesRead < fileSize) {
                buffer.clear();
                bytesRead += fileChannel.read(buffer);
                buffer.flip();
                int symbol;
                while (buffer.hasRemaining()) {
                    symbol = buffer.get();
                    if (symbol == 13) {
                        continue;
                    }
                    if (symbol == 10) {
                        bitArraySet.set(((long) baseNum << Byte.SIZE) | partNum);
                        baseNum = 0;
                        partNum = 0;
                        numberOfLines++;
                    } else {
                        if (symbol == '.') {
                            baseNum = (baseNum << Byte.SIZE) | partNum;
                            partNum = 0;
                        } else {
                            partNum = partNum * 10 + symbol - '0';
                        }
                    }
                }
                consumer.accept(bytesRead);
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