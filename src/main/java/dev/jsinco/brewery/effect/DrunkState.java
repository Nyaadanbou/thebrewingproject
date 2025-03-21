package dev.jsinco.brewery.effect;

public record DrunkState(int alcohol, long timestamp) {

    public DrunkState recalculate(int inverseDecayRate, long timestamp) {
        if (timestamp < this.timestamp) {
            return new DrunkState(this.alcohol, this.timestamp);
        }
        int diff = (int) (timestamp - this.timestamp);
        int alcohol = (this.alcohol - diff / inverseDecayRate);
        return new DrunkState(Math.max(0, Math.min(alcohol, 100)), timestamp);
    }

    public DrunkState addAlcohol(int alcohol) {
        return new DrunkState(Math.max(0, Math.min(this.alcohol + alcohol, 100)), timestamp);
    }
}
