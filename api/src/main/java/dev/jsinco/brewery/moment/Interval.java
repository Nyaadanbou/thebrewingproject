package dev.jsinco.brewery.moment;

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

    public static Interval parse(String string) throws IllegalArgumentException {
        if (!string.contains("-")) {
            int i = Integer.parseInt(string);
            return new Interval(i, i);
        }
        String[] split = string.split("-");
        return new Interval(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    @Override
    public String toString() {
        return start + "-" + stop;
    }
}
