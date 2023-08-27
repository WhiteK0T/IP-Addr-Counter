package org.tehlab.whitek0t.dao;

import org.tehlab.whitek0t.codeForReadme.IntContainer;

import java.util.BitSet;

public class DualBitSet implements IntContainer {
    private final BitSet positive = new BitSet(Integer.MAX_VALUE);
    private final BitSet negative = new BitSet(Integer.MAX_VALUE);

    @Override
    public void set(int i) {
        if (i > -1) {
            positive.set(i);
        } else {
            negative.set(~i);
        }
    }

    public boolean get(int i) {
        if (i > -1) {
            return positive.get(i);
        } else {
            return negative.get(~i);
        }
    }

    @Override
    public long countUnique() {
        return (long) positive.cardinality() + negative.cardinality();
    }
}