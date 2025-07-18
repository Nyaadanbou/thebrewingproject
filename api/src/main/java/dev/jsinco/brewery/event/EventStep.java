package dev.jsinco.brewery.event;

import dev.jsinco.brewery.util.Holder;

import java.util.List;

public interface EventStep {

    void execute(Holder.Player contextPlayer, List<EventStep> events, int index);
}
