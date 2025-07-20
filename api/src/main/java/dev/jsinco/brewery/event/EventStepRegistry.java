package dev.jsinco.brewery.event;

import java.util.EnumMap;
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
    private final EnumMap<NamedDrunkEvent, NamedEventStepFactory<? extends EventStep>> enumFactories = new EnumMap<>(NamedDrunkEvent.class);

    public <T extends EventStep> void register(Class<T> baseClass, EventStepFactory<T> factory) {
        if (factories.containsKey(baseClass)) {
            throw new IllegalArgumentException("Factory for " + baseClass.getName() + " is already registered.");
        }
        factories.put(baseClass, factory);
    }

    public <T extends EventStep> void register(NamedDrunkEvent event, NamedEventStepFactory<T> factory) {
        if (enumFactories.containsKey(event)) {
            throw new IllegalArgumentException("Factory for " + event.name() + " is already registered.");
        }
        enumFactories.put(event, factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends ExecutableEventStep> T upgrade(EventStep original) {
        EventStepFactory<T> factory = (EventStepFactory<T>) factories.get(original.getClass());
        return factory != null ? factory.create(original) : null;
    }

    @SuppressWarnings("unchecked")
    public <T extends EventStep> T upgrade(NamedDrunkEvent event) {
        NamedEventStepFactory<T> factory = (NamedEventStepFactory<T>) enumFactories.get(event);
        return factory != null ? factory.create() : null;
    }
}

