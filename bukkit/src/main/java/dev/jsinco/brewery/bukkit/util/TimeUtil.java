package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.configuration.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    public static String formatTime(long timeTicks) {
        String format = Config.config().timeFormat();

        long cookingMinuteTicks = Config.config().cauldrons().cookingMinuteTicks();

        long totalTicks = timeTicks;
        long totalSeconds = timeTicks * 60 / cookingMinuteTicks;
        long totalMinutes = timeTicks / cookingMinuteTicks;
        long totalHours = totalMinutes / 60;
        long totalDays = totalHours / 24;

        long ticks = timeTicks % cookingMinuteTicks;
        long seconds = (timeTicks % cookingMinuteTicks) * 60 / cookingMinuteTicks;
        long minutes = totalMinutes % 60;
        long hours = totalHours % 24;
        long days = totalDays;

        String result = format;

        result = result
                .replace("<ticks>", String.valueOf(ticks))
                .replace("<ticks-total>", String.valueOf(totalTicks))
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<seconds-total>", String.valueOf(totalSeconds))
                .replace("<minutes>", String.valueOf(minutes))
                .replace("<minutes-total>", String.valueOf(totalMinutes))
                .replace("<hours>", String.valueOf(hours))
                .replace("<hours-total>", String.valueOf(totalHours))
                .replace("<days>", String.valueOf(days))
                .replace("<days-total>", String.valueOf(totalDays));

        result = result
                .replaceAll("(?s)<if-ticks>(.*?)</if-ticks>", ticks > 0 ? "$1" : "")
                .replaceAll("(?s)<if-ticks-total>(.*?)</if-ticks-total>", totalTicks > 0 ? "$1" : "")
                .replaceAll("(?s)<if-seconds>(.*?)</if-seconds>", seconds > 0 ? "$1" : "")
                .replaceAll("(?s)<if-seconds-total>(.*?)</if-seconds-total>", totalSeconds > 0 ? "$1" : "")
                .replaceAll("(?s)<if-minutes>(.*?)</if-minutes>", minutes > 0 ? "$1" : "")
                .replaceAll("(?s)<if-minutes-total>(.*?)</if-minutes-total>", totalMinutes > 0 ? "$1" : "")
                .replaceAll("(?s)<if-hours>(.*?)</if-hours>", hours > 0 ? "$1" : "")
                .replaceAll("(?s)<if-hours-total>(.*?)</if-hours-total>", totalHours > 0 ? "$1" : "")
                .replaceAll("(?s)<if-days>(.*?)</if-days>", days > 0 ? "$1" : "")
                .replaceAll("(?s)<if-days-total>(.*?)</if-days-total>", totalDays > 0 ? "$1" : "");

        result = result
                .replaceAll("(?s)<if-!ticks>(.*?)</if-!ticks>", ticks > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!ticks-total>(.*?)</if-!ticks-total>", totalTicks > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!seconds>(.*?)</if-!seconds>", seconds > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!seconds-total>(.*?)</if-!seconds-total>", totalSeconds > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!minutes>(.*?)</if-!minutes>", minutes > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!minutes-total>(.*?)</if-!minutes-total>", totalMinutes > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!hours>(.*?)</if-!hours>", hours > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!hours-total>(.*?)</if-!hours-total>", totalHours > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!days>(.*?)</if-!days>", days > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!days-total>(.*?)</if-!days-total>", totalDays > 0 ? "" : "$1");

        Pattern pluralPattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pluralPattern.matcher(result);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            int sIndex = matcher.start();
            long value = 2; // default

            Pattern numberPattern = Pattern.compile("(\\d+)");
            Matcher numberMatcher = numberPattern.matcher(result.substring(0, sIndex));
            while (numberMatcher.find()) {
                value = Long.parseLong(numberMatcher.group(1)); // last number before brackets
            }

            String content = matcher.group(1); // content inside the brackets
            String replacement = (value == 1) ? "" : content;
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        result = sb.toString();

        return result;
    }

}
