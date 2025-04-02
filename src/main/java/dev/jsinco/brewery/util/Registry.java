package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Registry {

    public static final Map<BreweryKey, BarrelType> BARREL_TYPE = getBarrelTypes();
    public static final Map<BreweryKey, CauldronType> CAULDRON_TYPE = getEnumValues();
    public static final Map<BreweryKey, StructureMeta<?>> STRUCTURE_META = getStructureMeta();
    public static final Map<BreweryKey, StructureType> STRUCTURE_TYPE = getStructureTypes();
    public static final Map<BreweryKey, NamedDrunkEvent> DRUNK_EVENT = getDrunkEvents();

    private static Map<BreweryKey, NamedDrunkEvent> getDrunkEvents() {
        ImmutableMap.Builder<BreweryKey, NamedDrunkEvent> outputBuilder = ImmutableMap.builder();
        for (NamedDrunkEvent drunkEvent : NamedDrunkEvent.values()) {
            outputBuilder.put(drunkEvent.key(), drunkEvent);
        }
        return outputBuilder.build();
    }

    private static Map<BreweryKey, StructureMeta<?>> getStructureMeta() {
        try {
            ImmutableMap.Builder<BreweryKey, StructureMeta<?>> outputBuilder = ImmutableMap.builder();
            for (Field field : StructureMeta.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object staticField = field.get(null);
                if (staticField instanceof StructureMeta<?> structureMeta) {
                    outputBuilder.put(structureMeta.key(), structureMeta);
                }
            }
            return outputBuilder.build();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<BreweryKey, StructureType> getStructureTypes() {
        try {
            ImmutableMap.Builder<BreweryKey, StructureType> outputBuilder = ImmutableMap.builder();
            for (Field field : StructureType.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object staticField = field.get(null);
                if (staticField instanceof StructureType structureType) {
                    outputBuilder.put(structureType.key(), structureType);
                }
            }
            return outputBuilder.build();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<BreweryKey, CauldronType> getEnumValues() {
        ImmutableMap.Builder<BreweryKey, CauldronType> outputBuilder = ImmutableMap.builder();
        for (CauldronType barrelType : CauldronType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    private static Map<BreweryKey, BarrelType> getBarrelTypes() {
        ImmutableMap.Builder<BreweryKey, BarrelType> outputBuilder = ImmutableMap.builder();
        for (BarrelType barrelType : BarrelType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }
}
