package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class FeverNamedEvent extends NamedDrunkEvent {
    public FeverNamedEvent() {
        super(60, 20, 5, "fever");
    }
}
