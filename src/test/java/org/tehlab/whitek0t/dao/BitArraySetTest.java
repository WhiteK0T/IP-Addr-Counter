package org.tehlab.whitek0t.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tehlab.whitek0t.util.Util.getStringFromLongIpAddress;

public class BitArraySetTest {
    static BitArraySet bitArraySet = new BitArraySet(4294967297L);

    static LongStream factory() {
        return LongStream.range(1000L, 1100L);
    }

    @BeforeEach
    void setUp() {
        bitArraySet = new BitArraySet(4294967297L);
    }

    @ParameterizedTest
    @MethodSource(value = "factory")
    void set(Long index) {
        bitArraySet.set(index);
        boolean expected = bitArraySet.get(index);
        assertTrue(expected, getStringFromLongIpAddress(index));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0,0.0.0.0",
            "2147483647,127.255.255.255",
            "2147483648,128.0.0.0",
            "4294967295,255.255.255.254",
            "4294967296,255.255.255.255",
            "2437093124,145.67.23.4",
            "1857874172,110.188.232.252",
            "136447255,8.34.5.23",
            "1496712060,89.54.3.124",
            "1496712060,89.54.3.124",
            "53298949,3.45.71.5",
            "53298951,3.45.71.7",
            "53298951,3.45.71.7"
    })
    void get(Long index, String ipStr) {
        bitArraySet.set(index);
        boolean expected = bitArraySet.get(index);
        assertTrue(expected, ipStr);
    }


    @Test
    void cardinality() {
        LongStream.range(0, 1000L).forEach(index -> bitArraySet.set(index));
        long actual = bitArraySet.cardinality();
        assertEquals(1000, actual);
    }

    @Benchmark
    public long cardinalityBench() {
        return bitArraySet.cardinality();
    }
}