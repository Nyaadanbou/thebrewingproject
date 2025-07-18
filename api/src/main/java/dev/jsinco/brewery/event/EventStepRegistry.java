package dev.jsinco.brewery.event;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for {@link EventStep} classes that allows for "upgrading"
 * from one version to another. This class is mainly used for converting
 * EventStep instances to their child implementations which can be executed.
 */
public final class EventStepRegistry {

    @FunctionalInterface
    public interface EventStepFactory<T extends EventStep> {
        T create(EventStep original);
    }

    private final Map<Class<? extends EventStep>, EventStepFactory<? extends EventStep>> factories = new HashMap<>();

    public <T extends EventStep> void register(Class<T> baseClass, EventStepFactory<T> factory) {
        if (factories.containsKey(baseClass)) {
            throw new IllegalArgumentException("Factory for " + baseClass.getName() + " is already registered.");
        }
        factories.put(baseClass, factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventStep> T upgrade(T original) {
        EventStepFactory<T> factory = (EventStepFactory<T>) factories.get(original.getClass());
        return factory != null ? factory.create(original) : original;
    }
}

