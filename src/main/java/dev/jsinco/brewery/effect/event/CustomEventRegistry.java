package dev.jsinco.brewery.effect.event;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.util.BreweryKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomEventRegistry {

    Map<BreweryKey, CustomEvent> customEvents = new HashMap<>();

    public void registerCustomEvent(CustomEvent customEvent) {
        Preconditions.checkNotNull(customEvent);
        customEvents.put(customEvent.key(), customEvent);
    }

    public @Nullable CustomEvent getCustomEvent(BreweryKey key) {
        Preconditions.checkNotNull(key);
        return customEvents.get(key);
    }

    public Collection<CustomEvent> events() {
        return customEvents.values();
    }
}
