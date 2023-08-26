package org.tehlab.whitek0t.codeForReadme.standart;

import org.tehlab.whitek0t.codeForReadme.DualBitSet;
import org.tehlab.whitek0t.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;

public class sample2 {

    public static void main(String[] args) {
        LocalTime startTime = LocalTime.now();
        String filePath = "/mnt/dat200/ip_addresses";
        DualBitSet uniqueIPs = new DualBitSet();

        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                uniqueIPs.set((int) Util.getLongFromIpAddress_parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Количество уникальных IP-адресов: " + uniqueIPs.countUnique());
        LocalTime leadTime = LocalTime.now().minusNanos(startTime.toNanoOfDay());
        System.out.println("Lead time: " + leadTime.toString());
    }
}