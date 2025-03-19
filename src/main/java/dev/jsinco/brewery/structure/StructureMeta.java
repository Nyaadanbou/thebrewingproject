package dev.jsinco.brewery.structure;

import com.google.gson.JsonElement;
import dev.jsinco.brewery.util.Registry;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public record StructureMeta<V>(String key, Predicate<Object> validator, Function<JsonElement, V> deserializer) {

    public static final StructureMeta<Boolean> USE_BARREL_SUBSTITUTION = new StructureMeta<>("use_barrel_substitution", Boolean.class::isInstance, JsonElement::getAsBoolean);
    public static final StructureMeta<Integer> INVENTORY_SIZE = new StructureMeta<>("inventory_size", value ->
            value instanceof Integer integer && integer % 9 == 0 && integer > 0,
            JsonElement::getAsInt);
    public static final StructureMeta<StructureType> TYPE = new StructureMeta<>("type", StructureType.class::isInstance, jsonElement -> Registry.STRUCTURE_TYPE.get(jsonElement.getAsString().toLowerCase(Locale.ROOT)));
    public static final StructureMeta<String> TAGGED_MATERIAL = new StructureMeta<>("tagged_material", String.class::isInstance, JsonElement::getAsString);
    public static final StructureMeta<Long> PROCESS_TIME = new StructureMeta<>("process_time", Long.class::isInstance, JsonElement::getAsLong);

    @Override
    public String toString() {
        return "StructureMeta(" + key + ")";
    }
}
