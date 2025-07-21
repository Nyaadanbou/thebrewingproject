package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.step.ConditionalWaitStep.Condition;

import java.util.List;
import java.util.UUID;

public class ConditionalWaitStepExecutable implements ExecutableEventStep {

    private final Condition condition;

    public ConditionalWaitStepExecutable(Condition condition) {
        this.condition = condition;
    }


    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        if (condition == ConditionalWaitStep.Condition.JOIN && index + 1 < events.size()) {
            TheBrewingProject.getInstance().getDrunkEventExecutor().add(contextPlayer, events.subList(index + 1, events.size()));
        }
    }
}
