package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.DrunkEvent;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.BreweryKeyed;
import dev.jsinco.brewery.util.Holder;

import java.util.List;
import java.util.Locale;

// todo: unused
public enum NamedDrunkEventsEnum implements DrunkEvent, BreweryKeyed {
    PUKE(45, 45, 20),
    PASS_OUT(80, 80, 10),
    STUMBLE(25, 0, 100),
    CHICKEN(99, 50, 1),
    TELEPORT(90, 40, 2),
    DRUNK_MESSAGE(25, 0, 5),
    NAUSEA(60, 50, 50),
    DRUNKEN_WALK(60, 20, 20);


    private final int alcoholRequirement;
    private final int toxinsRequirement;
    private final int probabilityWeight;
    private final BreweryKey key;

    NamedDrunkEventsEnum(int alcoholRequirement, int toxinsRequirement, int probabilityWeight) {
        this.alcoholRequirement = alcoholRequirement;
        this.toxinsRequirement = toxinsRequirement;
        this.probabilityWeight = probabilityWeight;
        this.key = BreweryKey.parse(this.name().toLowerCase(Locale.ROOT));
    }


    public int alcoholRequirement() {
        return alcoholRequirement;
    }


    public int toxinsRequirement() {
        return toxinsRequirement;
    }

    @Override
    public BreweryKey key() {
        return this.key;
    }


    public String displayName() {
        return this.name().toLowerCase(Locale.ROOT);
    }


    public int probabilityWeight() {
        return probabilityWeight;
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}
