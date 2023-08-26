package org.tehlab.whitek0t.codeForReadme.streamapi;

import org.tehlab.whitek0t.codeForReadme.DualBitSet;
import org.tehlab.whitek0t.codeForReadme.IntContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.stream.Stream;

public class sample2 {
    public static void main(String[] args) {
        LocalTime startTime = LocalTime.now();
        String filePath = "/mnt/dat200/ip_addresses";
        ConverterStringToInt converterStringToInt = new ConverterStringToInt();

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            long uniqueIPCount = lines
                    .mapToInt(converterStringToInt)
                    .collect(DualBitSet::new, IntContainer::set, IntContainer::addAll)
                    .countUnique();

            System.out.println("Количество уникальных IP-адресов: " + uniqueIPCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalTime leadTime = LocalTime.now().minusNanos(startTime.toNanoOfDay());
        System.out.println("Lead time: " + leadTime.toString());
    }
}