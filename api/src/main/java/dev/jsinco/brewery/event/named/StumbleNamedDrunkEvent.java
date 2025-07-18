package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class StumbleNamedDrunkEvent extends NamedDrunkEvent {
    public StumbleNamedDrunkEvent() {
        super(25, 0, 100, "STUMBLE");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
