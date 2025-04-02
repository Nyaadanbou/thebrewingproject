package dev.jsinco.brewery.effect.event;

import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.random.WeightedProbabilityElement;

public sealed interface DrunkEvent extends WeightedProbabilityElement, EventStep permits CustomEvent, NamedDrunkEvent {

    int getAlcoholRequirement();

    int getToxinsRequirement();

    String getTranslation();

    BreweryKey key();
}
