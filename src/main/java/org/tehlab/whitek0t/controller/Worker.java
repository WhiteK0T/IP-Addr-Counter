package org.tehlab.whitek0t.controller;

import org.tehlab.whitek0t.dto.Result;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Worker {

    Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer);
}
