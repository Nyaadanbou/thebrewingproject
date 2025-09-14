package dev.jsinco.brewery.time;

public interface Duration {

    long durationTicks();


    record Minutes(long durationTicks) implements Duration {
    }

    record Ticks(long durationTicks) implements Duration {
    }


}
