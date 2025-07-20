package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.event.NamedDrunkEvent;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Registry<T extends BreweryKeyed> {

    public static final Registry<BarrelType> BARREL_TYPE = fromEnums(BarrelType.class);
    public static final Registry<CauldronType> CAULDRON_TYPE = fromEnums(CauldronType.class);
    public static final Registry<StructureMeta<?>> STRUCTURE_META = (Registry<StructureMeta<?>>) fromFields(StructureMeta.class);
    public static final Registry<StructureType> STRUCTURE_TYPE = (Registry<StructureType>) fromFields(StructureType.class);
    public static final Registry<NamedDrunkEvent> DRUNK_EVENT = fromEnums(NamedDrunkEvent.class);

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

    private static <T extends BreweryKeyed> Registry<T> fromParent(Class<T> tClass) {
        String packageName = tClass.getPackageName();
        return new Registry<>(assignableClasses(tClass, packageName));
    }

    // FIXME: This is okay for testing, but should not be a permanent solution.
    /**
     * Finds all classes in a specified package that are assignable to a given class.
     * This method will try to instantiate the class even if it has constructors with parameters.
     * @param tClass the class to check assignability against
     * @param packageName the package to search for classes
     * @return a set of instances of classes that are assignable to tClass
     * @param <T> the type of the class to check assignability against
     */
    public static <T> Set<T> assignableClasses(Class<T> tClass, String packageName) {
        ClassLoader classLoader = tClass.getClassLoader();

        try {
            return ClassPath.from(classLoader)
                    .getTopLevelClasses(packageName)
                    .stream()
                    .map(ClassPath.ClassInfo::getName)
                    .map(name -> {
                        try {
                            return Class.forName(name, false, classLoader);
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            throw new RuntimeException("Failed to load class: " + name, e);
                        }
                    })
                    .filter(clazz -> clazz != tClass &&
                            tClass.isAssignableFrom(clazz) &&
                            !Modifier.isAbstract(clazz.getModifiers()))
                    .map(clazz -> {
                        try {
                            // Try no-arg constructor first
                            Constructor<?> ctor = clazz.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            return tClass.cast(ctor.newInstance());
                        } catch (NoSuchMethodException e) {
                            // No no-arg constructor, try other constructors
                            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                            if (constructors.length == 0) {
                                throw new RuntimeException("No constructors found for class: " + clazz.getName());
                            }
                            Constructor<?> chosen = constructors[0]; // pick the first
                            chosen.setAccessible(true);

                            Class<?>[] paramTypes = chosen.getParameterTypes();
                            Object[] args = new Object[paramTypes.length];

                            for (int i = 0; i < paramTypes.length; i++) {
                                args[i] = getDefaultValue(paramTypes[i]);
                            }

                            try {
                                return tClass.cast(chosen.newInstance(args));
                            } catch (ReflectiveOperationException inner) {
                                throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), inner);
                            }
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), e);
                        }
                    })
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read classes from package: " + packageName, e);
        }
    }

    private static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        if (type == char.class) return '\0';
        throw new IllegalArgumentException("Unhandled primitive type: " + type.getName());
    }

}
