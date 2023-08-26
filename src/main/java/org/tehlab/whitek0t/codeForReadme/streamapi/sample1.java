package org.tehlab.whitek0t.codeForReadme.streamapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class sample1 {

    public static void main(String[] args) {
        String filePath = "/mnt/dat200/ip_addresses";

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            long uniqueIPCount = lines
                    .distinct()
                    .count();

            System.out.println("Количество уникальных IP-адресов: " + uniqueIPCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
