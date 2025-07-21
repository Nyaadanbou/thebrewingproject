package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;

import java.util.List;
import java.util.UUID;

public class CustomEventExecutable implements ExecutableEventStep {

    private final List<EventStep> steps;

    public CustomEventExecutable(List<EventStep> steps) {
        this.steps = steps;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(contextPlayer, steps);
    }
}
