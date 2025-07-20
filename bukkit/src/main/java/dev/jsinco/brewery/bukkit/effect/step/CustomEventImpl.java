package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.CustomEvent;
import dev.jsinco.brewery.util.BreweryKey;

import java.util.List;
import java.util.UUID;

public class CustomEventImpl extends CustomEvent implements ExecutableEventStep {

    public CustomEventImpl(List<EventStep> steps, int alcohol, int toxins, int probabilityWeight, String displayName, BreweryKey key) {
        super(steps, alcohol, toxins, probabilityWeight, displayName, key);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(contextPlayer, getSteps());
    }
}
