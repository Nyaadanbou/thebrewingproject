package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;

import java.util.List;
import java.util.UUID;

public class ConsumeStepExecutable implements ExecutableEventStep {

    private final int alcohol;
    private final int toxins;

    public ConsumeStepExecutable(int alcohol, int toxins) {
        this.alcohol = alcohol;
        this.toxins = toxins;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunksManager().consume(contextPlayer, alcohol, toxins);
    }

}
