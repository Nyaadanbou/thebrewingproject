package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.NamedDrunkEvent;

public final class NauseaNamedEvent extends NamedDrunkEvent {
    public NauseaNamedEvent() {
        super(60, 50, 50, "nausea");
    }
}
