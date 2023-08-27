package org.tehlab.whitek0t.controller;

import org.openjdk.jmh.annotations.*;
import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MINUTES)
@Warmup(iterations = 0)
@Measurement(iterations = 1, time = 30, timeUnit = TimeUnit.MINUTES)
public class WorkerBench {

    private final Path filePath = Paths.get("/mnt/dat200/ip_addresses");

    @Benchmark
    public Result uniqueIpAddressCounterNioMultiThreads() {
        UniqueIpAddressCounter_NIO_MultiThreads uniqueIpAddressCounterNioMultiThreads = new UniqueIpAddressCounter_NIO_MultiThreads();
        return uniqueIpAddressCounterNioMultiThreads.work(this.filePath, 6, aLong -> {
        });
    }

    @Benchmark
    public Result uniqueIpAddressCounterNio() {
        UniqueIpAddressCounter_NIO uniqueIpAddressCounterNio = new UniqueIpAddressCounter_NIO();
        return uniqueIpAddressCounterNio.work(this.filePath, 1, aLong -> {
        });
    }

    @Benchmark
    public Result uniqueIpAddressCounterBufferedReader() {
        UniqueIpAddressCounter_BufferedReader uniqueIpAddressCounterBufferedReader = new UniqueIpAddressCounter_BufferedReader();
        return uniqueIpAddressCounterBufferedReader.work(this.filePath, 1, aLong -> {
        });
    }

    @Benchmark
    public Result uniqueIpAddressCounterLines() {
        UniqueIpAddressCounter_Lines uniqueIpAddressCounterLines = new UniqueIpAddressCounter_Lines();
        return uniqueIpAddressCounterLines.work(this.filePath, 1, aLong -> {
        });
    }
}