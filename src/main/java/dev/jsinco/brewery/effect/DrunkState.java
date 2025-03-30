package dev.jsinco.brewery.effect;

public record DrunkState(int alcohol, int toxins, long timestamp) {

    public DrunkState recalculate(int inverseDecayRate, long timestamp) {
        if (timestamp < this.timestamp) {
            return new DrunkState(this.alcohol, this.toxins, this.timestamp);
        }
        int diff = (int) (timestamp - this.timestamp);
        int alcohol = (this.alcohol - diff / inverseDecayRate);
        int toxins = (this.toxins - diff / inverseDecayRate);
        return new DrunkState(Math.max(0, Math.min(alcohol, 100)), toxins, timestamp);
    }

    public DrunkState addAlcohol(int alcohol, int toxins) {
        return new DrunkState(
                Math.max(0, Math.min(this.alcohol + alcohol, 100)),
                Math.min(this.toxins + toxins, 100),
                timestamp
        );
    }
}
