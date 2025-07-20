package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class StumbleNamedEvent extends NamedDrunkEvent {
    public StumbleNamedEvent() {
        super(25, 0, 100, "stumble");
    }
}
