package org.logger;

import org.apache.commons.cli.*;
import org.logger.utils.FilterUtils;
import org.logger.utils.LogsGenerator;
import org.logger.utils.LogsUtil;

import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("g", true, "Generates number of records in logs directory");
        options.addOption("c", true, "Count of threads");
        options.addOption("p", true, "Path to logs. Default logs directory");
        options.addOption("o", true, "Path of output file. Prints in console by default");
        options.addOption("u", true, "User name");
        options.addOption("d", true, "Log date (yyyy/MM/dd)");
        options.addOption("s", true, "Start time (yyyy/MM/dd HH:mm:ss)");
        options.addOption("e", true, "End time (yyyy/MM/dd HH:mm:ss)");
        options.addOption("a", false, "Aggregate by user name input");


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            String logPath = "logs";
            String output = null;
            int countThreads = 1;
            if (cmd.hasOption("g")) {
                generateLog(Integer.parseInt(cmd.getOptionValue("g")));
            }
            if (cmd.hasOption("p")) {
                logPath = cmd.getOptionValue("p");
            }
            if (cmd.hasOption("o")){
                output = cmd.getOptionValue("o");
            }
            if (cmd.hasOption("c")){
                countThreads = Integer.parseInt(cmd.getOptionValue("c"));
            }
            if (cmd.hasOption("a")){
                if (output == null) {
                    LogsUtil.aggregate(logPath, FilterUtils.userExtractor(), countThreads);
                } else {
                    LogsUtil.aggregate(logPath, FilterUtils.userExtractor(), countThreads, output);
                }
            } else {
                Predicate<String> filter = findFilter(cmd);
                if (output == null) {
                    LogsUtil.printLogs(logPath, filter, countThreads);
                } else {
                    LogsUtil.printLogs(logPath, filter, countThreads, output);
                }
            }
        } catch (DateTimeParseException dte){
            System.out.println("Incorrect date format: " + dte.getMessage());
            printHelp(options);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("LogParser", options);
    }

    private static Predicate<String> findFilter(CommandLine cmd){
        if (cmd.hasOption("u")) {
            return FilterUtils.userName(cmd.getOptionValue("u"));
        } else if (cmd.hasOption("d")){
            return FilterUtils.byDate(FilterUtils.parseDate(cmd.getOptionValue("d")));
        } else if (cmd.hasOption("s") && cmd.hasOption("e")){
            return FilterUtils.byPeriod(
                    FilterUtils.parseDateTime(cmd.getOptionValue("s")),
                    FilterUtils.parseDateTime(cmd.getOptionValue("e")));
        } else {
            throw new IllegalArgumentException("Provide correct filter param");
        }

    }

    private static void generateLog(int records) {
        if (records >= 0) {
            LogsGenerator.generateLog(records);
        }
    }
}
