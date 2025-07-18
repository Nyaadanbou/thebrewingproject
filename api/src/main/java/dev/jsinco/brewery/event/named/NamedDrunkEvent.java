package dev.jsinco.brewery.event.named;


import dev.jsinco.brewery.event.DrunkEvent;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.BreweryKeyed;

import java.util.Locale;
import java.util.Random;

public sealed abstract class NamedDrunkEvent implements DrunkEvent, BreweryKeyed permits ChickenNamedDrunkEvent, DrunkMessageNamedDrunkEvent, DrunkenWalkNamedDrunkEvent, FeverNamedDrunkEvent, HallucinationNamedDrunkEvent, NauseaNamedDrunkEvent, PassOutNamedDrunkEvent, PukeNamedDrunkEvent, StumbleNamedDrunkEvent, TeleportNamedDrunkEvent {

    protected static final Random RANDOM = new Random();

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

    public int alcoholRequirement() {
        return alcoholRequirement;
    }


    public int toxinsRequirement() {
        return toxinsRequirement;
    }


    public String displayName() {
        return this.name.toLowerCase(Locale.ROOT);
    }


    public int probabilityWeight() {
        return probabilityWeight;
    }

    @Override
    public BreweryKey key() {
        return this.key;
    }
}
