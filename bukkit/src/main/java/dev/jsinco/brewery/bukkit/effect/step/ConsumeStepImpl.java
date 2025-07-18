package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.step.ConsumeStep;

import java.util.List;
import java.util.UUID;

public class ConsumeStepImpl extends ConsumeStep {

    public ConsumeStepImpl(int alcohol, int toxins) {
        super(alcohol, toxins);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunksManager().consume(contextPlayer, getAlcohol(), getToxins());
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(ConsumeStep.class, original -> {
            ConsumeStep event = (ConsumeStep) original;
            return new ConsumeStepImpl(event.getAlcohol(), event.getToxins());
        });
    }
}
