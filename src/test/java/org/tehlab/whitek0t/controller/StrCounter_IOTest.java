package org.tehlab.whitek0t.controller;

import org.junit.jupiter.api.Test;
import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

class StrCounter_IOTest {

    @Test
    void work() {
        Result expected = new Result(6, 8, LocalTime.MIN);
        StrCounter_IO strCounterIo = new StrCounter_IO();
        Result actual = strCounterIo.work(Path.of("src/test/resources/ip.txt"), 1, aLong -> {
        });
        assertThat(actual, samePropertyValuesAs(expected, "leadTime", "uniqueAddresses"));
    }
}