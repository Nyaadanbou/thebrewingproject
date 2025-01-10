package dev.jsinco.brewery.util.moment;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
}
