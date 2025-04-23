package dev.jsinco.brewery.brew;

import org.jetbrains.annotations.Nullable;

public interface BrewScore {

    @Nullable BrewQuality brewQuality();

    double getPartialScore(int stepIndex) throws IndexOutOfBoundsException;

    double score();

    double rawScore();

    boolean completed();

    String displayName();
}
