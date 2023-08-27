package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dao.BitArraySet;
import org.tehlab.whitek0t.dto.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class UniqueIpAddressCounter_NIO_MultiThreads implements Worker {
    public static int capacity = 1 << 27; //  134_217_728
    static BitArraySet bitArraySet = new BitArraySet((long) Integer.MAX_VALUE << 1); // 4_294_967_294
    private final List<BufferHandler> bufferHandlers = new ArrayList<>();
    private long filePartSize = 0;

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        long uniqueAddresses = 0;
        long numberOfLines = 0;
        LocalTime leadTime = LocalTime.MIN;
        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            long fileSize = fileChannel.size();
            init(numberOfThreads, fileSize);
            LocalTime startTime = LocalTime.now();
            boolean firstRun = true;
            while (this.bufferHandlers.get(0).currenPos <= filePartSize) {
                for (int i = 0; i < this.bufferHandlers.size(); i++) {
                    BufferHandler bufferHandler = this.bufferHandlers.get(i);
                    ByteBuffer byteBuffer = bufferHandler.buffer;
                    bufferHandler.semaphore.acquire();
                    byteBuffer.clear();
                    sliceBufferIfCapacityExceeded(fileSize, i, bufferHandler, byteBuffer);
                    bufferHandler.currenPos += fileChannel.read(byteBuffer, bufferHandler.currenPos);
                    byteBuffer.flip();
                    firstRun = isFirstRun(firstRun, i, bufferHandler, byteBuffer);
                    bufferHandler.semaphore.release();
                }
                consumer.accept(this.bufferHandlers.get(0).currenPos);
            }
            Thread.sleep(1500);
            numberOfLines = getNumberOfLinesAndStopThreads(numberOfLines);
            uniqueAddresses = bitArraySet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }

    private long getNumberOfLinesAndStopThreads(long numberOfLines) {
        for (BufferHandler bufferHandler : bufferHandlers) {
            bufferHandler.interrupt();
            numberOfLines += bufferHandler.numberOfLines;
        }
        return numberOfLines;
    }

    private void sliceBufferIfCapacityExceeded(long fileSize, int i, BufferHandler bufferHandler, ByteBuffer byteBuffer) {
        long endFilePart = bufferHandler.calculatedPos + this.filePartSize;
        if (bufferHandler.currenPos + capacity > endFilePart) {
            long newSizeBuf = getNewSizeBuf(fileSize, i, bufferHandler);
            bufferHandler.buffer = byteBuffer.slice(0, (int) newSizeBuf);
        }
    }

    private void init(int numberOfThreads, long fileSize) {
        this.filePartSize = fileSize / numberOfThreads;
        if (capacity > filePartSize) {
            capacity = (int) (filePartSize / 2);
        }
        long curPart = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);
            BufferHandler bufferHandler = new BufferHandler(byteBuffer, new Semaphore(1));
            bufferHandler.calculatedPos = curPart;
            bufferHandler.currenPos = curPart;
            bufferHandler.startPos = curPart;
            this.bufferHandlers.add(bufferHandler);
            curPart += this.filePartSize;
        }
    }

    private long getNewSizeBuf(long fileSize, int index, BufferHandler bufferHandler) {
        long newSizeBuf;
        if (index != this.bufferHandlers.size() - 1) {
            newSizeBuf = this.bufferHandlers.get(index + 1).startPos - bufferHandler.currenPos;
        } else {
            newSizeBuf = fileSize - bufferHandler.currenPos;
        }
        return newSizeBuf;
    }

    private boolean isFirstRun(boolean firstRun, int index, BufferHandler bufferHandler, ByteBuffer byteBuffer) {
        if (firstRun) {
            // поиск и сохранение стартовой позиции в буфере
            if (index != 0) {
                int symbol;
                while (byteBuffer.hasRemaining()) {
                    symbol = byteBuffer.get();
                    bufferHandler.startPos++;
                    if (symbol == 10) {
                        break;
                    }
                }
            }
            if (index == this.bufferHandlers.size() - 1) {
                firstRun = false;
            }
            bufferHandler.setDaemon(true);
            bufferHandler.start();
        }
        return firstRun;
    }

    class BufferHandler extends Thread {

        private long calculatedPos = 0; //посчитанная стартовая позиция
        private long currenPos = 0; // текущая позиция
        private long startPos = 0; // позиция от первого переноса строки "\n"
        private ByteBuffer buffer;
        private final Semaphore semaphore;
        private int baseNum = 0;
        private int partNum = 0;
        private long numberOfLines = 0;  // количество обработанных строк

        public BufferHandler(ByteBuffer buffer, Semaphore semaphore) {
            this.buffer = buffer;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            int symbol;
            while (!this.isInterrupted()) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    //throw new RuntimeException(e);
                    //TODO: решить проблему с InterruptedException, семафор периодически ловит
                }
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
                semaphore.release();
            }
        }
    }
}