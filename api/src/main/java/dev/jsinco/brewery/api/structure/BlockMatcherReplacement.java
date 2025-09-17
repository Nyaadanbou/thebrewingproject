package dev.jsinco.brewery.api.structure;

import dev.jsinco.brewery.api.util.Holder;

import java.util.Set;

public record BlockMatcherReplacement(Set<Holder.Material> alternatives, Holder.Material original) {
}
