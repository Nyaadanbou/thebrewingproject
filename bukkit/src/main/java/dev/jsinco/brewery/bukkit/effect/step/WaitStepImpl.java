package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.step.WaitStep;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class WaitStepImpl extends WaitStep {

    public WaitStepImpl(int durationTicks) {
        super(durationTicks);
    }

    public WaitStepImpl(String duration) {
        super(duration);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        if (index + 1 >= events.size()) {
            return;
        }

        final List<EventStep> eventsLeft = events.subList(index + 1, events.size());
        Bukkit.getScheduler().runTaskLater(
                TheBrewingProject.getInstance(),
                () -> TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(contextPlayer, eventsLeft),
                getDurationTicks()
        );
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(WaitStep.class, original -> {
            WaitStep event = (WaitStep) original;
            return new WaitStepImpl(event.getDurationTicks());
        });
    }
}
