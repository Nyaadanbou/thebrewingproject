package dev.jsinco.brewery.format;

import dev.jsinco.brewery.configuration.Config;

public class TimeFormatHeader {

    public static String get() {
        return switch (Config.config().language().toLanguageTag()) {
            //case "ru" -> russian();
            default -> english();
        };
    }

    private static String english() {
        return "#  +-----------------------------------------------------------------------------------+\n"
             + "#  | This file is for specifying how time intervals should be formatted and displayed. |\n"
             + "#  +-----------------------------------------------------------------------------------+\n"
             + "#\n"
             + "#  PLACEHOLDERS (will be replaced with the corresponding values):\n"
             + "#\n"
             + "#    Modular (leftover) values:\n"
             + "#      These show the value of a unit after larger units have been subtracted.\n"
             + "#      Available: <ticks>, <seconds>, <minutes>, <hours>, <days>, <years>, <decades>\n"
             + "#      Example: in \"1 hour 45 minutes\", <minutes> = 45.\n"
             + "#\n"
             + "#    Total values (independent of higher units):\n"
             + "#      These show the complete value of a unit, ignoring larger breakdowns.\n"
             + "#      Available: <ticks-total>, <seconds-total>, <minutes-total>, <hours-total>, <days-total>, <years-total>\n"
             + "#      Example: in \"1 hour 45 minutes\", <minutes-total> = 105.\n"
             + "#\n"
             + "#  CONDITIONAL BLOCKS:\n"
             + "#\n"
             + "#    <if-unit>...</if-unit>    ...Content is only shown if that unit is NOT zero\n"
             + "#    <if-!unit>...</if-!unit>  ...Content is only shown if that unit IS zero\n"
             + "#\n"
             + "#    Example: \"<if-hours-total><hours-total>:</if-hours-total><minutes>:<seconds>\"\n"
             + "#      => hours are only added for times above or equal to one hour (e.g. 1:45:00)\n"
             + "#\n"
             + "#  PLURALIZATION:\n"
             + "#\n"
             + "#    [text]  ...Shown only if the last number before it is NOT 1\n"
             + "#    {text}  ...Shown only if the last number before it IS 1\n"
             + "#\n"
             + "#    Example: \"<minutes> minute[s]\" => 1 minute / 2 minutes\n"
             + "#\n"
             + "#    [text]<5>  ...Shown only if the last number before it is NOT 5\n"
             + "#    {text}<5>  ...Shown only if the last number before it IS 5\n"
             + "#\n"
             + "#    Example: \"<ticks-total> {is five}<5>[is not five]<5>\" => 5 is five / 3 is not five\n"
             + "#\n"
             + "#    [text]<2-4>  ...Shown only if the last number before it is NOT in range 2-4\n"
             + "#    {text}<2-4>  ...Shown only if the last number before it IS in range 2-4\n"
             + "#\n"
             + "#    Example: \"<ticks-total> {is in range}<2-4>[is not in range]<2-4>\" => 3 is in range / 3 is not in range\n"
             + "#\n"
             + "#  EXAMPLES:\n"
             + "#\n"
             + "#    \"2 days, 0 hours, 17 minutes and 1 second\", \"45 minutes and 30 seconds\":\n"
             + "#\n"
             + "#      \"<if-days-total><days-total> day[s], <if-!hours><hours> hour[s], </if-!hours></if-days-total>\n"
             + "#       <if-hours><hours> hour[s], </if-hours><minutes> minute[s] and <seconds> second[s]\"\n"
             + "#\n"
             + "#    \"2 days, 17 minutes and 1 second\", \"45 minutes and 30 seconds\":\n"
             + "#\n"
             + "#      \"<if-days-total><days-total> day[s], </if-days-total><if-hours><hours> hour[s], </if-hours>\n"
             + "#       <minutes> minute[s] and <seconds> second[s]\"\n"
             + "#\n"
             + "#    \"26:45:30\", \"25:00\", \"00:05\":\n"
             + "#\n"
             + "#      \"<if-hours-total><hours-total>:</if-hours-total><minutes>:<seconds>\"\n"
             + "#\n"
             + "#    \"85:45\", \"00:15\":\n"
             + "#\n"
             + "#      \"<minutes-total>:<seconds>\"\n"
             + "#\n";
    }

    private static String russian() {
        return "";
    }
}
