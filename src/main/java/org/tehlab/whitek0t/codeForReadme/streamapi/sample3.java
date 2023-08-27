package org.tehlab.whitek0t.codeForReadme.streamapi;

import org.tehlab.whitek0t.codeForReadme.BitSetArray;
import org.tehlab.whitek0t.codeForReadme.IntContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.stream.Stream;

public class sample3 {
    public static void main(String[] args) {
        LocalTime startTime = LocalTime.now();
        String filePath = "/mnt/dat200/ip_addresses";
        OptimizedConverter optimizedConverter = new OptimizedConverter();

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            long uniqueIPCount = lines
                    .mapToInt(optimizedConverter)
                    .collect(BitSetArray::new, IntContainer::set, IntContainer::addAll)
                    .countUnique();

            System.out.println("Количество уникальных IP-адресов: " + uniqueIPCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalTime leadTime = LocalTime.now().minusNanos(startTime.toNanoOfDay());
        System.out.println("Lead time: " + leadTime.toString());
    }
}