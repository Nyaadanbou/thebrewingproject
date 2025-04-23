package dev.jsinco.brewery.moment;

public record PassedMoment(long moment) implements Moment {
    @Override
    public Interval withLastStep(long lastStep) {
        return new Interval(lastStep - moment, lastStep);
    }

    @Override
    public Interval withMovedEnding(long newEnd) {
        return new Interval(newEnd - moment, newEnd);
    }
}
