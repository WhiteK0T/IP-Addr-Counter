package org.tehlab.whitek0t.codeForReadme;

public interface IntContainer {
    // accumulator
    void set(int number);

    // combiner
    default void addAll(IntContainer other) {
        throw new UnsupportedOperationException();
    }

    long countUnique();
}