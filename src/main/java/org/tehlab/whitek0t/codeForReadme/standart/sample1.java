package org.tehlab.whitek0t.codeForReadme.standart;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class sample1 {

    public static void main(String[] args) {
        String filePath = "/mnt/dat200/ip_addresses";
        Set<String> uniqueIPs = new HashSet<>();

        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                uniqueIPs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Количество уникальных IP-адресов: " + uniqueIPs.size());
    }
}
