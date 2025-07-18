package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class DrunkenWalkNamedDrunkEvent extends NamedDrunkEvent {
    public DrunkenWalkNamedDrunkEvent() {
        super(60, 20, 20, "DRUNKEN_WALK");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
