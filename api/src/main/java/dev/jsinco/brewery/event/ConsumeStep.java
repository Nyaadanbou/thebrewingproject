package dev.jsinco.brewery.event;

public record ConsumeStep(int alcohol, int toxins) implements EventStep {
}
