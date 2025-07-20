package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class TeleportNamedEvent extends NamedDrunkEvent {
    public TeleportNamedEvent() {
        super(90, 40, 2, "teleport");
    }
}
