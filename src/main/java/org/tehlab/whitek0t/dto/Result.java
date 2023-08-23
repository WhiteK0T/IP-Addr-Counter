package org.tehlab.whitek0t.dto;

import java.time.LocalTime;

public record Result(long uniqueAddresses, long numberOfLines, LocalTime leadTime) {
}
