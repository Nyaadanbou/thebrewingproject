package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class MomentPdcType implements PersistentDataType<long[], Moment> {

    public static final MomentPdcType INSTANCE = new MomentPdcType();

    private MomentPdcType(){

    }

    @NotNull
    @Override
    public Class<long[]> getPrimitiveType() {
        return long[].class;
    }

    @NotNull
    @Override
    public Class<Moment> getComplexType() {
        return Moment.class;
    }

    @Override
    public long @NotNull [] toPrimitive(@NotNull Moment moment, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (moment instanceof Interval interval) {
            return new long[]{interval.start(), interval.stop()};
        }
        return new long[]{moment.moment()};
    }

    @NotNull
    @Override
    public Moment fromPrimitive(long @NotNull [] longs, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (longs.length == 1) {
            return new PassedMoment(longs[0]);
        }
        return new Interval(longs[0], longs[1]);
    }
}
