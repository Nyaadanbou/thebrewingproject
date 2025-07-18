package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class DrunkMessageNamedDrunkEvent extends NamedDrunkEvent {

    public DrunkMessageNamedDrunkEvent() {
        super(25, 0, 5, "DRUNK_MESSAGE");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
