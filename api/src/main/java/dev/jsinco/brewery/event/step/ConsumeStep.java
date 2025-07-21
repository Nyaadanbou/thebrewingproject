package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;

public record ConsumeStep(int alcohol, int toxins) implements EventStep {

}