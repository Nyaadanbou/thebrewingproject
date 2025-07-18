package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class PassOutNamedDrunkEvent extends NamedDrunkEvent {
    public PassOutNamedDrunkEvent() {
        super(80, 80, 10, "PASS_OUT");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
