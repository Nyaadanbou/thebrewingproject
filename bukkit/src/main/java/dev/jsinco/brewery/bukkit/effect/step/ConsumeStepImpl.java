package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.step.ConsumeStep;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public class ConsumeStepImpl extends ConsumeStep {

    public ConsumeStepImpl(int alcohol, int toxins) {
        super(alcohol, toxins);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunksManager().consume(contextPlayer.value(), getAlcohol(), getToxins());
    }
}
