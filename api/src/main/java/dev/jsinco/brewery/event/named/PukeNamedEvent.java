package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class PukeNamedEvent extends NamedDrunkEvent {
    public PukeNamedEvent() {
        super(45, 45, 20, "puke");
    }
}
