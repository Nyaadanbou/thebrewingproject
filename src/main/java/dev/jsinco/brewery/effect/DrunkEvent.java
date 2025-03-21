package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.util.RandomUtil;
import dev.jsinco.brewery.util.Registry;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum DrunkEvent implements RandomUtil.WeightedProbabilityElement {
    PUKE(35, 20),
    KICK(80, 5),
    STUMBLE(25, 25);

    private final int alcohol;
    private final int probabilityWeight;

    DrunkEvent(int alcohol, int probabilityWeight) {
        this.alcohol = alcohol;
        this.probabilityWeight = probabilityWeight;
    }

    public String key() {
        return Registry.brewerySpacedKey(this.name().toLowerCase(Locale.ROOT));
    }
}
