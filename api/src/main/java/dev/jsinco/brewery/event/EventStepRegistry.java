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

    public interface NamedEventStepFactory<T extends EventStep> {
        T create();
    }

    private final Map<Class<? extends EventStep>, EventStepFactory<? extends EventStep>> factories = new HashMap<>();
    private final Map<NamedDrunkEvent, NamedEventStepFactory<? extends EventStep>> namedFactories = new HashMap<>();

    public <T extends EventStep, K extends EventStep> void register(Class<T> baseClass, EventStepFactory<K> factory) {
        if (factories.containsKey(baseClass)) {
            throw new IllegalArgumentException("Factory for " + baseClass.getName() + " is already registered.");
        }
        factories.put(baseClass, factory);
    }

    public <K extends EventStep> void register(NamedDrunkEvent key, NamedEventStepFactory<K> factory) {
        if (namedFactories.containsKey(key)) {
            throw new IllegalArgumentException("Factory for " + key + " is already registered.");
        }
        namedFactories.put(key, factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends ExecutableEventStep> T upgrade(EventStep original) {
        if (original instanceof NamedDrunkEvent namedDrunkEvent) {
            return upgrade(namedDrunkEvent);
        }

        EventStepFactory<T> factory = (EventStepFactory<T>) factories.get(original.getClass());
        return factory != null ? factory.create(original) : null;
    }

    @SuppressWarnings("unchecked")
    public <T extends ExecutableEventStep> T upgrade(NamedDrunkEvent original) {
        NamedEventStepFactory<T> factory = (NamedEventStepFactory<T>) namedFactories.get(original);
        return factory != null ? factory.create() : null;
    }
}

