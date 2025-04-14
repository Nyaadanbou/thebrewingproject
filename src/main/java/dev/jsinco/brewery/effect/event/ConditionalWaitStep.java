package dev.jsinco.brewery.effect.event;

import java.util.Locale;

public record ConditionalWaitStep(Condition condition) implements EventStep{


    public static EventStep parse(String condition) {
        return new ConditionalWaitStep(Condition.valueOf(condition.toUpperCase(Locale.ROOT)));
    }

    public enum Condition {
        JOIN
    }
}
