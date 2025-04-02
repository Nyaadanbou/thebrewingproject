package dev.jsinco.brewery.effect.event;

import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.util.BreweryKey;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

//TODO add customizability here from the config
public enum NamedDrunkEvent implements DrunkEvent {
    PUKE(35, 45, 20),
    PASS_OUT(80, 80, 5),
    STUMBLE(25, 0, 25),
    CHICKEN(99, 50, 1),
    TELEPORT(90, 40, 7),
    DRUNK_MESSAGE(25, 0, 15);

    private final int alcohol;
    private final int toxins;
    @Getter
    private final int probabilityWeight;

    NamedDrunkEvent(int alcohol, int toxins, int probabilityWeight) {
        this.alcohol = alcohol;
        this.toxins = toxins;
        this.probabilityWeight = probabilityWeight;
    }

    public BreweryKey key() {
        return BreweryKey.parse(name().toLowerCase(Locale.ROOT));
    }

    @Override
    public int getAlcoholRequirement() {
        return alcohol;
    }

    @Override
    public int getToxinsRequirement() {
        return toxins;
    }

    public @NotNull String getTranslation() {
        return TranslationsConfig.EVENT_TYPES.get(this.name().toLowerCase(Locale.ROOT));
    }
}
