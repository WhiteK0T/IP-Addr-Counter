package org.tehlab.whitek0t.util;

public final class Util {

    private Util() {
    }

    public static String sizeFormat(Long size) {
        if (size / 1_073_741_824 > 0) return Math.round((0.0 + size) / 1_073_741_824) + "Gb";
        if (size / 10_485_760 > 0) return Math.round((0.0 + size) / 1_048_576) + "Mb";
        if (size / 1_048_576 > 0) return Math.round((0.0 + size) / 1024) + "Kb";
        return String.valueOf(size);
    }
}
