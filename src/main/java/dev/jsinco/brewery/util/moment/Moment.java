package dev.jsinco.brewery.util.moment;


import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public interface Moment {
    PdcType PDC_TYPE = new PdcType();

    int SECOND = 20;
    int MINUTE = SECOND * 60;
    int AGING_YEAR = MINUTE * 20; //TODO: make this a setting

    long moment();

    default int minutes() {
        return (int) (moment() / MINUTE);
    }

    default int agingYears() {
        return (int) (moment() / AGING_YEAR);
    }

    class PdcType implements PersistentDataType<long[], Moment> {

        private PdcType() {

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
}
