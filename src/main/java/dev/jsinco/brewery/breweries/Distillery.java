package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.vector.BreweryLocation;

public interface Distillery {

    long getStartTime();

    BreweryLocation getLocation();

    State getState();

    enum State {
        PAUSED, RUNNING, INVALID
    }
}
