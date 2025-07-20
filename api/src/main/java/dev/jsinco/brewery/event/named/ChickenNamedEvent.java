package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class ChickenNamedEvent extends NamedDrunkEvent {
    public ChickenNamedEvent() {
        super(99, 50, 1, "chicken");
    }
}
