package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.step.WaitStep;
import dev.jsinco.brewery.util.Holder;
import org.bukkit.Bukkit;

import java.util.List;

public class WaitStepImpl extends WaitStep {

    public WaitStepImpl(int durationTicks) {
        super(durationTicks);
    }

    public WaitStepImpl(String duration) {
        super(duration);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        if (index + 1 >= events.size()) {
            return;
        }

        final List<EventStep> eventsLeft = events.subList(index + 1, events.size());
        Bukkit.getScheduler().runTaskLater(
                TheBrewingProject.getInstance(),
                () -> TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(contextPlayer.value(), eventsLeft),
                durationTicks()
        );
    }
}
