package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public class ConditionalWaitStepImpl extends ConditionalWaitStep {

    public ConditionalWaitStepImpl(Condition condition) {
        super(condition);
    }

    public ConditionalWaitStepImpl(String condition) {
        super(condition);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        if (getCondition() == ConditionalWaitStep.Condition.JOIN && index + 1 < events.size()) {
            TheBrewingProject.getInstance().getDrunkEventExecutor().add(contextPlayer.value(), events.subList(index + 1, events.size()));
        }
    }
}
