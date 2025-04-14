package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.configuration.Config;

public record DrunkState(int alcohol, int toxins, double walkSpeedSquared, long timestamp) {

    public DrunkState recalculate(long timestamp) {
        if (timestamp < this.timestamp) {
            return new DrunkState(this.alcohol, this.toxins, this.walkSpeedSquared, this.timestamp);
        }
        int diff = (int) (timestamp - this.timestamp);
        // Assume that the drunk value does not get recalculated that much
        int alcohol = (this.alcohol - diff / Config.ALCOHOL_DECAY_RATE);
        int toxins = (this.toxins - diff / Config.TOXIN_DECAY_RATE);
        return new DrunkState(Math.max(0, Math.min(alcohol, 100)), Math.max(0, Math.min(toxins, 100)), this.walkSpeedSquared, timestamp);
    }

    public DrunkState addAlcohol(int alcohol, int toxins) {
        return new DrunkState(
                Math.max(0, Math.min(this.alcohol + alcohol, 100)),
                Math.min(this.toxins + toxins, 100),
                this.walkSpeedSquared,
                timestamp
        );
    }

    public DrunkState withSpeedSquared(double speedSquared) {
        return new DrunkState(
                this.alcohol, this.toxins, speedSquared, this.timestamp
        );
    }
}
