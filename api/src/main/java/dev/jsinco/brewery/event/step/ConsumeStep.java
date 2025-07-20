package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;

public class ConsumeStep implements EventStep {

    private final int alcohol;
    private final int toxins;

    public ConsumeStep(int alcohol, int toxins) {
        this.alcohol = alcohol;
        this.toxins = toxins;
    }

    public int getAlcohol() {
        return alcohol;
    }

    public int getToxins() {
        return toxins;
    }
}