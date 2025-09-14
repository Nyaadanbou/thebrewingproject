package dev.jsinco.brewery.api.event.step;

import dev.jsinco.brewery.api.event.EventStepProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record WaitStep(int durationTicks) implements EventStepProperty {

}
