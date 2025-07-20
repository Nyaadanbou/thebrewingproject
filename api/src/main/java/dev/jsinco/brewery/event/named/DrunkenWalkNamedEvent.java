package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class DrunkenWalkNamedEvent extends NamedDrunkEvent {
    public DrunkenWalkNamedEvent() {
        super(60, 20, 20, "drunken_walk");
    }
}
