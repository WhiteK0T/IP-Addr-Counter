package org.tehlab.whitek0t.controller;

import org.junit.jupiter.api.Test;
import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

class StrCounter_NIOTest {

    @Test
    void work() {
        Result expected = new Result(6, 8, LocalTime.MIN);
        StrCounter_NIO strCounterNio = new StrCounter_NIO();
        Result actual = strCounterNio.work(Path.of("src/test/resources/ip.txt"), 1, aLong -> {
        });
        assertThat(actual, samePropertyValuesAs(expected, "leadTime", "uniqueAddresses"));
    }
}