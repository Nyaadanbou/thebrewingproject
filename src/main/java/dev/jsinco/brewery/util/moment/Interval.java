package dev.jsinco.brewery.util.moment;

import dev.jsinco.brewery.util.Util;

public record Interval(long start, long stop) implements Moment {

    @Override
    public long moment() {
        return stop - start;
    }

    @Override
    public Interval withLastStep(long lastStep) {
        return new Interval(start, lastStep);
    }

    @Override
    public Interval withMovedEnding(long newEnd) {
        return new Interval(newEnd - stop + start, newEnd);
    }

    public static Interval parse(String string) {
        if (!string.contains("-")) {
            int i = Util.getIntDefaultZero(string);
            return new Interval(i, i);
        }
        String[] split = string.split("-");
        return new Interval(Util.getIntDefaultZero(split[0]), Util.getIntDefaultZero(split[1]));
    }

    @Override
    public String toString() {
        return start + "-" + stop;
    }
}
