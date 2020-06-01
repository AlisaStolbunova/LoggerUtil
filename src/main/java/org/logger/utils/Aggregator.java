package org.logger.utils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public class Aggregator implements Runnable {

    private final File file;
    private final Map<String, Integer> map;
    private final Function<String, String> keyExtractor;
    private final Semaphore semaphore;

    public Aggregator(File file, Map<String, Integer> map, Function<String, String> keyExtractor, Semaphore semaphore) {
        this.file = file;
        this.map = map;
        this.keyExtractor = keyExtractor;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            LogsUtil.readFileAndAggregateByKey(file, keyExtractor, map);
        } finally {
            semaphore.release();
        }
    }
}
