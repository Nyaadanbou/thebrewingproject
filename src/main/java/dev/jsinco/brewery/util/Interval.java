package dev.jsinco.brewery.util;

import com.google.common.base.Preconditions;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public record Interval(long start, long stop) {

    public static final PdcType PDC_TYPE = new PdcType();

    public Interval {
        Preconditions.checkArgument(stop >= start);
    }

    public Interval withStart(long start) {
        return new Interval(start, stop);
    }

    public Interval withStop(long stop) {
        return new Interval(start, stop);
    }

    public long diff() {
        return stop - start;
    }

    public static class PdcType implements PersistentDataType<long[], Interval> {

        private PdcType() {

        }

        @NotNull
        @Override
        public Class<long[]> getPrimitiveType() {
            return long[].class;
        }

        @NotNull
        @Override
        public Class<Interval> getComplexType() {
            return Interval.class;
        }

        @Override
        public long @NotNull [] toPrimitive(@NotNull Interval interval, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return new long[]{interval.start(), interval.stop()};
        }

        @NotNull
        @Override
        public Interval fromPrimitive(long @NotNull [] longs, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return new Interval(longs[0], longs[1]);
        }
    }
}
