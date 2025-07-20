package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.event.NamedDrunkEvent;
import dev.jsinco.brewery.event.named.ChickenNamedEvent;
import dev.jsinco.brewery.event.named.DrunkMessageNamedEvent;
import dev.jsinco.brewery.event.named.DrunkenWalkNamedEvent;
import dev.jsinco.brewery.event.named.FeverNamedEvent;
import dev.jsinco.brewery.event.named.HallucinationNamedEvent;
import dev.jsinco.brewery.event.named.KaboomNamedEvent;
import dev.jsinco.brewery.event.named.NauseaNamedEvent;
import dev.jsinco.brewery.event.named.PassOutNamedEvent;
import dev.jsinco.brewery.event.named.PukeNamedEvent;
import dev.jsinco.brewery.event.named.StumbleNamedEvent;
import dev.jsinco.brewery.event.named.TeleportNamedEvent;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Registry<T extends BreweryKeyed> {

    public static final Registry<BarrelType> BARREL_TYPE = fromEnums(BarrelType.class);
    public static final Registry<CauldronType> CAULDRON_TYPE = fromEnums(CauldronType.class);
    public static final Registry<StructureMeta<?>> STRUCTURE_META = (Registry<StructureMeta<?>>) fromFields(StructureMeta.class);
    public static final Registry<StructureType> STRUCTURE_TYPE = (Registry<StructureType>) fromFields(StructureType.class);
    public static final Registry<NamedDrunkEvent> DRUNK_EVENT = fromClasses(ChickenNamedEvent.class, DrunkenWalkNamedEvent.class, DrunkMessageNamedEvent.class, FeverNamedEvent.class, HallucinationNamedEvent.class, KaboomNamedEvent.class, NauseaNamedEvent.class, PassOutNamedEvent.class, PukeNamedEvent.class, StumbleNamedEvent.class, TeleportNamedEvent.class);

    private final ImmutableMap<BreweryKey, T> backing;

    private Registry(Collection<T> values) {
        ImmutableMap.Builder<BreweryKey, T> registryBacking = ImmutableMap.builder();
        values.forEach(value -> registryBacking.put(value.key(), value));
        this.backing = registryBacking.build();
    }

    public Collection<T> values() {
        return backing.values();
    }

    public @Nullable T get(BreweryKey key) {
        return backing.get(key);
    }

    public boolean containsKey(BreweryKey key) {
        return backing.containsKey(key);
    }


    private static <E extends Enum<E> & BreweryKeyed> Registry<E> fromEnums(Class<E> enumClass) {
        return new Registry<>(Arrays.stream(enumClass.getEnumConstants()).toList());
    }

    private static <T extends BreweryKeyed> Registry<? extends T> fromFields(Class<T> tClass) {
        try {
            List<T> tList = new ArrayList<>();
            for (Field field : tClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object staticField = field.get(null);
                if (tClass.isInstance(staticField)) {
                    tList.add(tClass.cast(staticField));
                }
            }
            return new Registry<>(tList);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends BreweryKeyed> Registry<T> fromClasses(Class<? extends T>... classes) {
        List<T> tList = new ArrayList<>();
        for (Class<? extends T> clazz : classes) {
            try {
                Constructor<? extends T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                T instance = constructor.newInstance();
                tList.add(instance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), e);
            }
        }
        return new Registry<>(tList);
    }

}
