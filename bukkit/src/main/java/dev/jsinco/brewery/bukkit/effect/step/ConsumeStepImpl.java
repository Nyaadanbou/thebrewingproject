package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.ConsumeStep;

import java.util.List;
import java.util.UUID;

public class ConsumeStepImpl extends ConsumeStep implements ExecutableEventStep {

    public ConsumeStepImpl(int alcohol, int toxins) {
        super(alcohol, toxins);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunksManager().consume(contextPlayer, getAlcohol(), getToxins());
    }

}
