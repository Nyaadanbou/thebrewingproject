package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

public non-sealed class TeleportNamedDrunkEvent extends NamedDrunkEvent {
    public TeleportNamedDrunkEvent() {
        super(90, 40, 2, "TELEPORT");
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
