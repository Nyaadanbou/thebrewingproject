package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ConditionalWaitStep implements EventStep {

    private final Condition condition;

    public ConditionalWaitStep(Condition condition) {
        this.condition = condition;
    }

    public ConditionalWaitStep(String condition) {
        this.condition = Condition.valueOf(condition.toUpperCase(Locale.ROOT));
    }

    public Condition getCondition() {
        return condition;
    }

    public enum Condition {
        JOIN
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        throw new IllegalEventStepCall();
    }

    @Override
    public void register(EventStepRegistry registry) {
        throw new IllegalEventStepCall();
    }
}
