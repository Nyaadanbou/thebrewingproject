package dev.jsinco.brewery.event;

import java.util.List;
import java.util.UUID;

public interface EventStep {

    void execute(UUID contextPlayer, List<EventStep> events, int index);

    void register(EventStepRegistry registry);
}
