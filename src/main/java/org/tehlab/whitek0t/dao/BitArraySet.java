package org.tehlab.whitek0t.dao;

public class BitArraySet {
    private final long[] bits;
    private final long size;

    public BitArraySet(long size) {
        if (size < 0 || size > 137438953472L) {
            throw new IllegalArgumentException("Size cannot be less than zero or greater than 137 438 953 472 size = " + size);
        }
        this.size = size;
        long arraySize = (long) Math.ceil((double) size / 64);
        bits = new long[(int) arraySize];
    }

    public void set(long index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of range. index = " + index);
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        bits[arrayIndex] |= (1L << bitIndex);
    }

    public boolean get(long index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of range");
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        return ((bits[arrayIndex] >> bitIndex) & 1) == 1;
    }

    public long cardinality() {
        long sum = 0;
        for (long bit : bits) sum += Long.bitCount(bit);
        return sum;
    }
}