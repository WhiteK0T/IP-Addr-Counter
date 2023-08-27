package org.tehlab.whitek0t.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tehlab.whitek0t.util.Util.getStringFromLongIpAddress;

public class DualBitSetTest {

    static DualBitSet dualBitSet = new DualBitSet();

    static IntStream factory() {
        return IntStream.range(1000, 1100);
    }

    @BeforeEach
    void setUp() {
        dualBitSet = new DualBitSet();
    }

    @ParameterizedTest
    @MethodSource(value = "factory")
    void set(int index) {
        dualBitSet.set(index);
        boolean expected = dualBitSet.get(index);
        assertTrue(expected, getStringFromLongIpAddress(index));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0,0.0.0.0",
            "2147483647,127.255.255.255",
            "2147483648,128.0.0.0",
            "4294967294,255.255.255.254",
            "4294967295,255.255.255.255",
            "2437093124,145.67.23.4",
            "1857874172,110.188.232.252",
            "136447255,8.34.5.23",
            "1496712060,89.54.3.124",
            "1496712060,89.54.3.124",
            "53298949,3.45.71.5",
            "53298951,3.45.71.7",
            "53298951,3.45.71.7"
    })
    void get(long index, String ipStr) {
        dualBitSet.set((int) index);
        boolean expected = dualBitSet.get((int) index);
        assertTrue(expected, ipStr);
    }


    @Test
    void cardinality() {
        IntStream.range(0, 1000).forEach(index -> dualBitSet.set(index));
        long actual = dualBitSet.countUnique();
        assertEquals(1000, actual);
    }

    @Benchmark
    public long cardinalityBench() {
        return dualBitSet.countUnique();
    }
}