package dev.jsinco.brewery.bukkit.configuration.serializer;

import dev.jsinco.brewery.api.event.IntegrationEvent;
import dev.jsinco.brewery.api.event.EventData;
import dev.jsinco.brewery.bukkit.api.integration.EventIntegration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A composite serializer that delegates to all registered {@link EventIntegration} instances.
 * During deserialization, it tries each integration until one successfully parses the event string.
 */
public class IntegrationEventSerializer implements ObjectSerializer<IntegrationEvent> {

    private final Supplier<Set<EventIntegration<?>>> integrationsSupplier;

    public IntegrationEventSerializer(Supplier<Set<EventIntegration<?>>> integrationsSupplier) {
        this.integrationsSupplier = integrationsSupplier;
    }

    @Override
    public boolean supports(@NonNull Class<? super IntegrationEvent> type) {
        return IntegrationEvent.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull IntegrationEvent object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        for (EventIntegration<?> integration : integrationsSupplier.get()) {
            if (integration.eClass().isInstance(object)) {
                @SuppressWarnings("unchecked")
                EventIntegration<IntegrationEvent> typed = (EventIntegration<IntegrationEvent>) integration;
                EventData eventData = typed.convertToData(object);
                data.setValue(eventData.serialized());
                return;
            }
        }
    }

    @Override
    public IntegrationEvent deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        String serialized = data.getValue(String.class);
        if (serialized == null) {
            return null;
        }
        EventData eventData = EventData.deserialize(serialized);
        for (EventIntegration<?> integration : integrationsSupplier.get()) {
            var result = integration.convertToEvent(eventData);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }
}
