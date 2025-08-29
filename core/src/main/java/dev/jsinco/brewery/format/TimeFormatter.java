package dev.jsinco.brewery.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatter {

    public static String format(long timeTicks, TimeFormat format) {
        return  format(timeTicks, format, TimeModifier.NORMAL);
    }

    public static String format(long timeTicks, TimeFormat format, TimeModifier modifier) {

        double tpm = modifier.getTicksPerMinute();

        long totalMinutes = (long) Math.floor(timeTicks / tpm);
        long totalSeconds = (long) Math.floor(timeTicks * 60.0 / tpm);

        long totalHours = totalMinutes / 60;
        long totalDays = totalHours / 24;
        long totalYears = totalDays / 365;
        long totalDecades = totalYears / 10;

        long minutes = totalMinutes % 60;
        long hours = totalHours % 24;
        long days = totalDays % 365;
        long years = totalYears % 10;

        long ticks = (long) (timeTicks - Math.floor(totalMinutes * tpm));
        long seconds = (long) Math.floor((timeTicks - Math.floor(totalMinutes * tpm)) * 60.0 / tpm);

        // totalTicks=timeTicks and decades=totalDecades

        String result = format.get();

        result = result
                .replace("<ticks>", String.valueOf(ticks))
                .replace("<ticks-total>", String.valueOf(timeTicks))
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<seconds-total>", String.valueOf(totalSeconds))
                .replace("<minutes>", String.valueOf(minutes))
                .replace("<minutes-total>", String.valueOf(totalMinutes))
                .replace("<hours>", String.valueOf(hours))
                .replace("<hours-total>", String.valueOf(totalHours))
                .replace("<days>", String.valueOf(days))
                .replace("<days-total>", String.valueOf(totalDays))
                .replace("<years>", String.valueOf(years))
                .replace("<years-total>", String.valueOf(totalYears))
                .replace("<decades>", String.valueOf(totalDecades))
                .replace("<decades-total>", String.valueOf(totalDecades));

        result = result
                .replaceAll("(?s)<if-ticks>(.*?)</if-ticks>", ticks > 0 ? "$1" : "")
                .replaceAll("(?s)<if-ticks-total>(.*?)</if-ticks-total>", timeTicks > 0 ? "$1" : "")
                .replaceAll("(?s)<if-seconds>(.*?)</if-seconds>", seconds > 0 ? "$1" : "")
                .replaceAll("(?s)<if-seconds-total>(.*?)</if-seconds-total>", totalSeconds > 0 ? "$1" : "")
                .replaceAll("(?s)<if-minutes>(.*?)</if-minutes>", minutes > 0 ? "$1" : "")
                .replaceAll("(?s)<if-minutes-total>(.*?)</if-minutes-total>", totalMinutes > 0 ? "$1" : "")
                .replaceAll("(?s)<if-hours>(.*?)</if-hours>", hours > 0 ? "$1" : "")
                .replaceAll("(?s)<if-hours-total>(.*?)</if-hours-total>", totalHours > 0 ? "$1" : "")
                .replaceAll("(?s)<if-days>(.*?)</if-days>", days > 0 ? "$1" : "")
                .replaceAll("(?s)<if-days-total>(.*?)</if-days-total>", totalDays > 0 ? "$1" : "")
                .replaceAll("(?s)<if-years>(.*?)</if-years>", years > 0 ? "$1" : "")
                .replaceAll("(?s)<if-years-total>(.*?)</if-years-total>", totalYears > 0 ? "$1" : "")
                .replaceAll("(?s)<if-decades>(.*?)</if-decades>", totalDecades > 0 ? "$1" : "")
                .replaceAll("(?s)<if-decades-total>(.*?)</if-decades-total>", totalDecades > 0 ? "$1" : "");

        result = result
                .replaceAll("(?s)<if-!ticks>(.*?)</if-!ticks>", ticks > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!ticks-total>(.*?)</if-!ticks-total>", timeTicks > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!seconds>(.*?)</if-!seconds>", seconds > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!seconds-total>(.*?)</if-!seconds-total>", totalSeconds > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!minutes>(.*?)</if-!minutes>", minutes > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!minutes-total>(.*?)</if-!minutes-total>", totalMinutes > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!hours>(.*?)</if-!hours>", hours > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!hours-total>(.*?)</if-!hours-total>", totalHours > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!days>(.*?)</if-!days>", days > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!days-total>(.*?)</if-!days-total>", totalDays > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!years>(.*?)</if-!years>", years > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!years-total>(.*?)</if-!years-total>", totalYears > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!decades>(.*?)</if-!decades>", totalDecades > 0 ? "" : "$1")
                .replaceAll("(?s)<if-!decades-total>(.*?)</if-!decades-total>", totalDecades > 0 ? "" : "$1");

        Pattern blockPattern = Pattern.compile("\\[(.*?)\\]|\\{(.*?)\\}");
        Pattern numberPattern = Pattern.compile("(\\d+)");

        Matcher matcher = blockPattern.matcher(result);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            int blockStart = matcher.start();
            long value = 2; // default

            Matcher numberMatcher = numberPattern.matcher(result.substring(0, blockStart));
            while (numberMatcher.find()) {
                value = Long.parseLong(numberMatcher.group(1));
            }

            boolean isCurly = matcher.group(2) != null;
            String content = isCurly ? matcher.group(2) : matcher.group(1);
            String replacement = isCurly == (value == 1) ? content : "";

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        result = sb.toString();

        return result;
    }

}
