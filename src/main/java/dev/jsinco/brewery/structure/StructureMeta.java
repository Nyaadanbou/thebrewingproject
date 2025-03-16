package dev.jsinco.brewery.structure;

import com.google.gson.JsonElement;
import dev.jsinco.brewery.breweries.Barrel;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public record StructureMeta<T, V>(String key, Predicate<Object> validator, Function<JsonElement, V> deserializer) {

    public static StructureMeta<Barrel, Boolean> USE_BARREL_SUBSTITUTION = new StructureMeta<>("use_barrel_substitution", Boolean.class::isInstance, JsonElement::getAsBoolean);
    public static StructureMeta<Barrel, Integer> INVENTORY_SIZE = new StructureMeta<>("inventory_size", value ->
            value instanceof Integer integer && integer % 9 == 0 && integer > 0,
            JsonElement::getAsInt);
    public static StructureMeta<?, StructureType> TYPE = new StructureMeta<>("type", StructureType.class::isInstance, jsonElement -> StructureType.valueOf(jsonElement.getAsString().toUpperCase(Locale.ROOT)));

    @Override
    public String toString() {
        return "StructureMeta(" + key + ")";
    }
}
