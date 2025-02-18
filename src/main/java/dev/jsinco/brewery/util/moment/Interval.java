package dev.jsinco.brewery.util.moment;

import dev.jsinco.brewery.util.Util;

public record Interval(long start, long stop) implements Moment {

    public Interval withStart(long start) {
        return new Interval(start, stop);
    }

    public Interval withStop(long stop) {
        return new Interval(start, stop);
    }

    public long moment() {
        return stop - start;
    }

    public static Interval parse(String string) {
        if (!string.contains("-")) {
            int i = Util.getIntDefaultZero(string);
            return new Interval(i, i);
        }
        String[] split = string.split("-");
        return new Interval(Util.getIntDefaultZero(split[0]), Util.getIntDefaultZero(split[1]));
    }
}
