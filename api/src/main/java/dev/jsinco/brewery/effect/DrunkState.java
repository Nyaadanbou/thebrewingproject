package dev.jsinco.brewery.effect;

public interface DrunkState {

    DrunkState recalculate(long timeStamp);

    DrunkState addAlcohol(int alcohol, int toxins);

    DrunkState withSpeedSquared(double speedSquared);

    DrunkState withPassOut(long kickedTimestamp);

    int alcohol();

    int toxins();

    double walkSpeedSquared();

    long timestamp();

    long kickedTimestamp();
}
