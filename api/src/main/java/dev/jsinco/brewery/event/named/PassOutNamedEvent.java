package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class PassOutNamedEvent extends NamedDrunkEvent {
    public PassOutNamedEvent() {
        super(80, 80, 10, "pass_out");
    }
}
