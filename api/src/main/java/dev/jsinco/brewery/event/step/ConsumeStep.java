package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

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

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}