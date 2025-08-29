package dev.jsinco.brewery.format;

import dev.jsinco.brewery.configuration.Config;

public enum TimeModifier {

    NORMAL(1200.0),
    COOKING((double) Config.config().cauldrons().cookingMinuteTicks()),
    AGING((double) Config.config().barrels().agingYearTicks() / (365.0 * 24 * 60));

    private final double ticksPerMinute;
    TimeModifier(double tpm) {
        this.ticksPerMinute = tpm;
    }

    public double getTicksPerMinute() {
        return ticksPerMinute;
    }
}
