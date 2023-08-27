package org.tehlab.whitek0t.controller;

import org.junit.jupiter.api.Test;
import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

class UniqueIpAddressCounter_NIOTest {

    @Test
    void work() {
        Result expected = new Result(6, 8, LocalTime.MIN);
        UniqueIpAddressCounter_NIO uniqueIpAddressCounterNio = new UniqueIpAddressCounter_NIO();
        Result actual = uniqueIpAddressCounterNio.work(Path.of("src/test/resources/ip.txt"), 1, aLong -> {
        });
        assertThat(actual, samePropertyValuesAs(expected, "leadTime"));
    }
}