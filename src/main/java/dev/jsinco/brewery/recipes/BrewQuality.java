package dev.jsinco.brewery.recipes;

import lombok.Getter;

@Getter
public enum BrewQuality {
    BAD(0xFF0000),
    GOOD(0xFFA500),
    EXCELLENT(0x00FF00);

    private final int color;

    BrewQuality(int color) {
        this.color = color;
    }
}
