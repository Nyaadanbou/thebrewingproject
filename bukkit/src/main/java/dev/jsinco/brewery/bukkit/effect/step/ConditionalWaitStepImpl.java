package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;

import java.util.List;
import java.util.UUID;

public class ConditionalWaitStepImpl extends ConditionalWaitStep implements ExecutableEventStep {

    public ConditionalWaitStepImpl(Condition condition) {
        super(condition);
    }

    public ConditionalWaitStepImpl(String condition) {
        super(condition);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        if (getCondition() == ConditionalWaitStep.Condition.JOIN && index + 1 < events.size()) {
            TheBrewingProject.getInstance().getDrunkEventExecutor().add(contextPlayer, events.subList(index + 1, events.size()));
        }
    }
}
