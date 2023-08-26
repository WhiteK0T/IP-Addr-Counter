package org.tehlab.whitek0t.codeForReadme.streamapi;

import java.util.function.ToIntFunction;

public class OptimizedConverter implements ToIntFunction<CharSequence> {

    @Override
    public int applyAsInt(CharSequence ipAddress) {
        int base = 0;
        int part = 0;

        for (int i = 0, n = ipAddress.length(); i < n; ++i) {
            char symbol = ipAddress.charAt(i);
            if (symbol == '.') {
                base = (base << Byte.SIZE) | part;
                part = 0;
            } else {
                part = part * 10 + symbol - '0';
            }
        }
        return (base << Byte.SIZE) | part;
    }
}