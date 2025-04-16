package dev.jsinco.brewery.effect.event;

public record ConsumeStep(int alcohol, int toxins) implements EventStep {
}
