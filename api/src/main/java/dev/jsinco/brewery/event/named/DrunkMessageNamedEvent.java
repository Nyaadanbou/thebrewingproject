package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class DrunkMessageNamedEvent extends NamedDrunkEvent {
    public DrunkMessageNamedEvent() {
        super(25, 0, 5, "drunk_message");
    }
}
