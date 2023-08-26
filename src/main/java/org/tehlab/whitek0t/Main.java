package org.tehlab.whitek0t;

import org.tehlab.whitek0t.controller.*;
import org.tehlab.whitek0t.dto.Result;
import org.tehlab.whitek0t.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static int numberOfThreads = 4;
    private static Path filePath;
    private static long fileSize;

    public static void main(String[] args) {
        System.out.println("Program for counting unique IP addresses.");
        System.out.println("UIAC version " + "1.0" + "   By WhiteK0T  https://github.com/WhiteK0T/IP-Addr-Counter");
        System.out.println("UIAC [number of threads 1-20] filename with ip addresses");
        if (args.length == 0 || args.length > 2) {
            System.out.println("Not enough arguments to run");
            System.exit(1);
        }

        Worker worker = getWorkerFromArguments(args);

        System.out.println("Worker : " + worker.getClass().getSimpleName());
        System.out.println();
        long filePartSize = fileSize / numberOfThreads;
        Result result = worker.work(filePath, numberOfThreads, aLong -> {
            double percent = (double) aLong / filePartSize * 100;
            System.out.printf("\rDone: %.0f%%", percent);
        });
        System.out.println("\n");
        System.out.println("Lead time: " + result.leadTime().toString());
        System.out.println("Number of IP addresses in the file: " + result.numberOfLines());
        System.out.println();
        System.out.println("Number of unique IP addresses: " + result.uniqueAddresses());
    }

    private static Worker getWorkerFromArguments(String[] args) {
        try {
            switch (args.length) {
                case 1 -> filePath = Paths.get(args[0]);
                case 2 -> {
                    numberOfThreads = Integer.parseInt(args[0]);
                    if (numberOfThreads < 1 || numberOfThreads > 20) {
                        System.out.println("Wrong number of threads: " + numberOfThreads);
                        System.exit(1);
                    }
                    filePath = Paths.get(args[1]);
                }
                default -> {
                    System.out.println("Too many arguments: " + args.length);
                    System.exit(1);
                }
            }
            fileSize = Files.size(filePath);
            System.out.println("Threads : " + numberOfThreads);
            System.out.println("Path : " + filePath.toAbsolutePath().getParent().toString());
            System.out.println("File : " + filePath.getFileName().toString());
            System.out.println("Size : " + Util.sizeFormat(fileSize));
        } catch (InvalidPathException e) {
            System.out.println("Wrong path or filename: " + (args.length == 1 ? args[0] : args[1]));
            System.exit(1);
        } catch (NumberFormatException e) {
            System.out.println("Wrong number of threads: " + args[0]);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numberOfThreads == 1 ? new UniqueIpAddressCounter_NIO() : new UniqueIpAddressCounter_NIO_MultiThreads();
    }
}