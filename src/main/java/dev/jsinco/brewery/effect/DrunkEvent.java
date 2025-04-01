package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.util.RandomUtil;
import dev.jsinco.brewery.util.Registry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Getter
public enum DrunkEvent implements RandomUtil.WeightedProbabilityElement {
    PUKE(35, 20),
    PASS_OUT(80, 5),
    STUMBLE(25, 25),
    CHICKEN(99, 1),
    TELEPORT(90, 7),
    MESSAGE(25, 15);

    private final int alcohol;
    private final int probabilityWeight;

    DrunkEvent(int alcohol, int probabilityWeight) {
        this.alcohol = alcohol;
        this.probabilityWeight = probabilityWeight;
    }

    public String key() {
        return Registry.brewerySpacedKey(this.name().toLowerCase(Locale.ROOT));
    }

    public @NotNull String translation() {
        return TranslationsConfig.EVENT_TYPES.get(this.name().toLowerCase(Locale.ROOT));
    }
}
