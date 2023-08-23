package org.tehlab.whitek0t.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openjdk.jmh.annotations.*;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Fork(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
public class UtilTest {

    @ParameterizedTest
    @CsvSource(value = {
            "1024, 1024b",
            "1_410_241, 1377Kb",
            "50_000_000, 48Mb",
            "6_999_000_000, 7Gb"
    })
    void sizeFormat(long size, String expected) {
        String actual = Util.sizeFormat(size);
        assertThat(actual, equalTo(expected));
    }

    @ParameterizedTest
    @CsvFileSource(numLinesToSkip = 1, resources = "/ip.csv")
    void getStringFromLongIpAddress(long ip, String strIp) {
        String actual = Util.getStringFromLongIpAddress(ip);
        assertThat(actual, equalTo(strIp));
    }

    @ParameterizedTest
    @CsvFileSource(numLinesToSkip = 1, resources = "/ip.csv")
    void getLongFromIpAddress_InetAddress(long expected, String strIp) {
        Long actual = Assertions.assertDoesNotThrow(() -> Util.getLongFromIpAddress_InetAddress(strIp));
        assertThat(actual, equalTo(expected));
    }

    @ParameterizedTest
    @CsvFileSource(numLinesToSkip = 1, resources = "/ip.csv")
    void getLongFromIpAddress_parseInt(long expected, String strIp) {
        Long actual = Util.getLongFromIpAddress_parseInt(strIp);
        assertThat(actual, equalTo(expected));
    }

    @Benchmark
    public long benchmarkParseInt() {
        return Util.getLongFromIpAddress_parseInt("255.25.155.0");
    }

    @Benchmark
    public long benchmarkInetAddress() throws UnknownHostException {
        return Util.getLongFromIpAddress_InetAddress("255.25.155.0");
    }
}