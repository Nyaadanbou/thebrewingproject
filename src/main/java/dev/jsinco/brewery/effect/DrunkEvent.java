package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.util.RandomUtil;
import dev.jsinco.brewery.util.Registry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Getter
public enum DrunkEvent implements RandomUtil.WeightedProbabilityElement {
    PUKE(35, 20, TranslationsConfig.PUKE_EVENT),
    KICK(80, 5, TranslationsConfig.KICK_EVENT),
    STUMBLE(25, 25, TranslationsConfig.STUMBLE_EVENT);

    private final int alcohol;
    private final int probabilityWeight;
    private final String translation;

    DrunkEvent(int alcohol, int probabilityWeight, String translation) {
        this.alcohol = alcohol;
        this.probabilityWeight = probabilityWeight;
        this.translation = translation;
    }

    public String key() {
        return Registry.brewerySpacedKey(this.name().toLowerCase(Locale.ROOT));
    }

    public @NotNull String translation() {
        return this.translation;
    }
}
