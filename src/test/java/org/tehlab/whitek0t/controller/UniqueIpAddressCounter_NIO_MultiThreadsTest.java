package org.tehlab.whitek0t.controller;

import org.junit.jupiter.api.Test;
import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

class UniqueIpAddressCounter_NIO_MultiThreadsTest {

    @Test
    void work() {
        Result expected = new Result(6, 8, LocalTime.MIN);
        UniqueIpAddressCounter_NIO_MultiThreads uniqueIpAddressCounterNioMultiThreads = new UniqueIpAddressCounter_NIO_MultiThreads();
        Result actual = uniqueIpAddressCounterNioMultiThreads.work(Path.of("src/test/resources/ip.txt"), 2, aLong -> {
        });
        assertThat(actual, samePropertyValuesAs(expected, "leadTime"));
    }
}