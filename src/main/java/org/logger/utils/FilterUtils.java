package org.logger.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.function.Predicate;

public class FilterUtils {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static Predicate<String> userName(String username) {
        return line -> line.contains("[" + username + "]");
    }

    public static Predicate<String> byDate(LocalDate time) {
        return line -> time.equals(parseDate(line.split(" ")[0]));
    }

    public static Predicate<String> byPeriod(LocalDateTime start, LocalDateTime end) {
        return line -> {
            LocalDateTime date = parseDateTime(extractStringDate(line));
            return date.isAfter(start) && date.isBefore(end);
        };
    }

    public static Function<String, String> userExtractor(){
        return line -> line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]"));
    }

    private static String extractStringDate(String line) {
        String[] str = line.split(" ");
        return str[0] + " " + str[1];
    }

    public static LocalDateTime parseDateTime(String date) {
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, dateFormatter);
    }
}
