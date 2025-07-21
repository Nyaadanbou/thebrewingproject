package dev.jsinco.brewery.event;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public interface ExecutableEventStep extends EventStep {

    Random RANDOM = new Random();

    void execute(UUID contextPlayer, List<EventStep> events, int index);

}
