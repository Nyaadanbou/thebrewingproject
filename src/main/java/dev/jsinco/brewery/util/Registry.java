package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.effect.DrunkEvent;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Registry {

    public static final Map<String, BarrelType> BARREL_TYPE = getBarrelTypes();
    public static final Map<String, CauldronType> CAULDRON_TYPE = getEnumValues();
    public static final Map<String, StructureMeta<?>> STRUCTURE_META = getStructureMeta();
    public static final Map<String, StructureType> STRUCTURE_TYPE = getStructureTypes();
    public static final Map<String, DrunkEvent> DRUNK_EVENT = getDrunkEvents();

    private static Map<String, DrunkEvent> getDrunkEvents() {
        ImmutableMap.Builder<String, DrunkEvent> outputBuilder = ImmutableMap.builder();
        for (DrunkEvent drunkEvent : DrunkEvent.values()) {
            outputBuilder.put(drunkEvent.key(), drunkEvent);
        }
        return outputBuilder.build();
    }

    private static Map<String, StructureMeta<?>> getStructureMeta() {
        try {
            ImmutableMap.Builder<String, StructureMeta<?>> outputBuilder = ImmutableMap.builder();
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

    private static Map<String, StructureType> getStructureTypes() {
        try {
            ImmutableMap.Builder<String, StructureType> outputBuilder = ImmutableMap.builder();
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

    private static Map<String, CauldronType> getEnumValues() {
        ImmutableMap.Builder<String, CauldronType> outputBuilder = ImmutableMap.builder();
        for (CauldronType barrelType : CauldronType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    private static Map<String, BarrelType> getBarrelTypes() {
        ImmutableMap.Builder<String, BarrelType> outputBuilder = ImmutableMap.builder();
        for (BarrelType barrelType : BarrelType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    public static String brewerySpacedKey(String key) {
        return "brewery:" + key;
    }
}
