package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class PukeNamedDrunkEvent extends NamedDrunkEvent {

    public PukeNamedDrunkEvent() {
        super(45, 45, 20, "PUKE");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
