package org.tehlab.whitek0t.codeForReadme;

public class BitSetArray implements IntContainer {
    private final long[] bits;
    private final long size;

    private long count = 0;

    public BitSetArray() {
        this.size = (long) Integer.MAX_VALUE << 1;
        long arraySize = (long) Math.ceil((double) size / 64);
        bits = new long[(int) arraySize];
    }

    public long getCount() {
        return count;
    }

    @Override
    public void set(int i) {
        long index = i & 0xFFFFFFFFL;
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index out of range. index = " + index);
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        bits[arrayIndex] |= (1L << bitIndex);
        this.count++;
    }

    public boolean get(int i) {
        long index = i & 0xFFFFFFFFL;
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index out of range. index = " + index);
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        return ((bits[arrayIndex] >> bitIndex) & 1) == 1;
    }

    @Override
    public long countUnique() {
        long sum = 0;
        for (long bit : bits) sum += Long.bitCount(bit);
        return sum;
    }
}