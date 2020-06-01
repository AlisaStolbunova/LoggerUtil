package org.logger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class LogsGenerator {
    private static final Logger logger = LoggerFactory.getLogger(LogsGenerator.class);
    private static final List<String> users = Arrays.asList("bakerhighlight","insaneevaluate","zeniascare","scamearthy",
            "callieclipper","bathrobeashy","angrinessalleged","dealingdefiance","quotablejoker","puebloharper",
            "meersafterglow","hurtfulabout","cearssnowplow");

    public static void generateLog(int records)  {
        for (int i = 0; i < records; i++) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("[{}]: message number {}", users.get((int) (Math.random() * users.size())), i);
        }
    }
}
