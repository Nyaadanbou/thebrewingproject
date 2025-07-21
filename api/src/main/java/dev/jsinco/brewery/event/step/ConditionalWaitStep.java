package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;

import java.util.Locale;

public record ConditionalWaitStep(Condition condition) implements EventStep {

    public static ConditionalWaitStep parse(String condition) {
        return new ConditionalWaitStep(Condition.valueOf(condition.toUpperCase(Locale.ROOT)));
    }

    public Condition getCondition() {
        return condition;
    }

    public enum Condition {
        JOIN
    }
}
