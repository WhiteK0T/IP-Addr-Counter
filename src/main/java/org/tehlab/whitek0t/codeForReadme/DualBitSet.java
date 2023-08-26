package org.tehlab.whitek0t.codeForReadme;

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

    @Override
    public long countUnique() {
        return (long) positive.cardinality() + negative.cardinality();
    }
}