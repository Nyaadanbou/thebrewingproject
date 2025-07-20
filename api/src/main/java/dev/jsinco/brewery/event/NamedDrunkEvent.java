package dev.jsinco.brewery.event;

import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.BreweryKeyed;

import java.util.Locale;

public abstract class NamedDrunkEvent implements DrunkEvent, BreweryKeyed {

    private final int alcoholRequirement;
    private final int toxinsRequirement;
    private final int probabilityWeight;
    private final String name;
    private final BreweryKey key;

    public NamedDrunkEvent(int alcoholRequirement, int toxinsRequirement, int probabilityWeight, String name) {
        this.alcoholRequirement = alcoholRequirement;
        this.toxinsRequirement = toxinsRequirement;
        this.probabilityWeight = probabilityWeight;
        this.name = name;
        this.key = BreweryKey.parse(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public int alcoholRequirement() {
        return alcoholRequirement;
    }

    @Override
    public int toxinsRequirement() {
        return toxinsRequirement;
    }

    @Override
    public BreweryKey key() {
        return this.key;
    }

    @Override
    public String displayName() {
        return this.name.toLowerCase(Locale.ROOT);
    }

    @Override
    public int probabilityWeight() {
        return probabilityWeight;
    }
}
