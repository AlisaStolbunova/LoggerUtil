package org.logger.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

public class FileScanner implements Runnable {
    private final File file;
    private final BufferedWriter writer;
    private final Predicate<String> filter;
    private final Semaphore semaphore;

    public FileScanner(File file, BufferedWriter writer, Predicate<String> filter, Semaphore semaphore) {
        this.file = file;
        this.writer = writer;
        this.filter = filter;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            LogsUtil.readFileByFilter(file, filter, writer);
        } finally {
            semaphore.release();
        }
    }


}
