package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class NauseaNamedDrunkEvent extends NamedDrunkEvent {
    public NauseaNamedDrunkEvent() {
        super(60, 50, 50, "NAUSEA");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
