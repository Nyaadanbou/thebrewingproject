package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class WaitStepExecutable implements ExecutableEventStep {

    private final int durationTicks;

    public WaitStepExecutable(int durationTicks) {
        this.durationTicks = durationTicks;
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
                durationTicks
        );
    }

}
