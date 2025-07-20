package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class KaboomNamedEvent extends NamedDrunkEvent {
    public KaboomNamedEvent() {
        super(99, 60, 1, "kaboom");
    }
}
