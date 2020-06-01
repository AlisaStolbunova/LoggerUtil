package org.logger.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;

public class LogsUtil {

    public static void printLogs(String logPath, Predicate<String> filter) {
        printLogs(logPath, filter, 1);
    }

    public static void printLogs(String logPath, Predicate<String> filter, String output) {
        printLogs(logPath, filter, 1, output);
    }

    public static void printLogs(String logPath, Predicate<String> filter, int countThreads) {
        scan(logPath, filter, countThreads, null);
    }

    public static void printLogs(String logPath, Predicate<String> filter, int countThreads, String output) {
        scan(logPath, filter, countThreads, output);
    }

    public static void aggregate(String logPath, Function<String, String> keyExtractor, int countThreads){
        scan(logPath, keyExtractor, countThreads)
                .forEach((key, value) -> System.out.println(key + " " + value));
    }

    public static void aggregate(String logPath, Function<String, String> keyExtractor, int countThreads, String output){
        Map<String, Integer> map = scan(logPath, keyExtractor, countThreads);
        writeOutput(map, output);
    }

    private static Map<String, Integer> scan(String logPath, Function<String, String> keyExtractor, int countThreads) {
        File dir = new File(logPath);
        File[] files;
        if (dir.exists()) {
            if (dir.isDirectory() && (files = dir.listFiles()) != null) {
                return processFiles(files, countThreads, keyExtractor);
            } else {
                return processFiles(new File[]{dir}, countThreads, keyExtractor);
            }
        } else {
            throw new IllegalArgumentException("File or directory not found");
        }
    }

    private static void scan(String logPath, Predicate<String> filter, int countThreads, String output) {
        File dir = new File(logPath);
        File[] files;
        if (dir.exists()) {
            if (dir.isDirectory() && (files = dir.listFiles()) != null) {
                processFiles(files, filter, countThreads, output);
            } else {
                processFiles(new File[]{dir}, filter, countThreads, output);
            }
        } else {
            throw new IllegalArgumentException("File or directory not found");
        }
    }

    private static void processFiles(File[] files, Predicate<String> filter, int countThreads, String output) {
        countThreads = countThreads > 0 ? countThreads : 1;
        Semaphore semaphore = new Semaphore(countThreads);

        BufferedWriter writer = null;
        try {
            if (output != null) {
                writer = Files.newBufferedWriter(Paths.get(output));
            }
            List<Thread> threads = new ArrayList<>();
            for (File file : files) {
                semaphore.acquire();
                Thread thread = new Thread(new FileScanner(file, writer, filter, semaphore));
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException | IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private static Map<String, Integer> processFiles(File[] files, int countThreads, Function<String, String> keyExtractor) {
        countThreads = countThreads > 0 ? countThreads : 1;
        Semaphore semaphore = new Semaphore(countThreads);
        List<Thread> threads = new ArrayList<>();
        Map<String, Integer> map = new ConcurrentHashMap<>();
        try {
            for (File file : files) {
                semaphore.acquire();
                Thread thread = new Thread(new Aggregator(file, map, keyExtractor, semaphore));
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        return map;
    }

    static List<String> readFileByFilter(File file, Predicate<String> filter, BufferedWriter writer) {
        List<String> result = new ArrayList<>();
        try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
            while (it.hasNext()) {
                String line = it.nextLine();
                if (filter.test(line)) {
                    if (writer != null) {
                        writer.write(line + "\n");
                    } else
                        System.out.println(line);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return result;
    }

    static void readFileAndAggregateByKey(File file, Function<String, String> keyExtractor, Map<String, Integer> map) {
        try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
            while (it.hasNext()) {
                String line = it.nextLine();
                map.compute(keyExtractor.apply(line), (key, value) -> value == null ? 1 : value + 1);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void writeOutput(Map<String, Integer> map, String outputPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            for (Map.Entry<String, Integer> keyValue : map.entrySet()) {
                writer.write(keyValue.getKey() + " " + keyValue.getValue() + "\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
