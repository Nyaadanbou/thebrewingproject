package dev.jsinco.brewery.configuration.serializers;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.event.*;
import dev.jsinco.brewery.event.named.NamedDrunkEvent;
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.step.ConsumeStep;
import dev.jsinco.brewery.event.step.SendCommand;
import dev.jsinco.brewery.event.step.Teleport;
import dev.jsinco.brewery.event.step.WaitStep;
import dev.jsinco.brewery.moment.Interval;
import dev.jsinco.brewery.moment.Moment;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Logging;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.vector.BreweryLocation;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EventRegistrySerializer implements TypeSerializer<CustomEventRegistry> {
    @Override
    public CustomEventRegistry deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        CustomEventRegistry output = new CustomEventRegistry();
        Map<Object, ? extends ConfigurationNode> children = node.childrenMap();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : children.entrySet()) {
            output.registerCustomEvent(readEvent(children, entry.getKey().toString(), Set.of()));
        }
        return output;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable CustomEventRegistry obj, @NotNull ConfigurationNode node) throws SerializationException {
        Map<String, Map<String, Object>> output = new HashMap<>();
        for (CustomEvent event : obj.events()) {
            Map<String, Object> eventData = new HashMap<>();
            if (event.alcoholRequirement() != 0) {
                eventData.put("alcohol", event.alcoholRequirement());
            }
            if (event.toxinsRequirement() != 0) {
                eventData.put("toxins", event.toxinsRequirement());
            }
            if (event.probabilityWeight() != 0) {
                eventData.put("probability-weight", event.probabilityWeight());
            }
            eventData.put("steps", event.getSteps().stream()
                    .map(this::stepToConfigSerializable)
                    .toList()
            );
            output.put(event.key().key(), eventData);
        }
        node.set(output);
    }

    private static <T> T readWithDefault(Map<Object, ? extends ConfigurationNode> data, String key, FunctionThatThrows<ConfigurationNode, T, SerializationException> function, T defaultValue) throws SerializationException {
        return data.containsKey(key) ? function.apply(data.get(key)) : defaultValue;
    }

    private CustomEvent readEvent(Map<Object, ? extends ConfigurationNode> customEvents, String eventName, Set<String> banned) throws SerializationException {
        try {
            Preconditions.checkArgument(eventName != null, "Undefined event name");
            if (banned.contains(eventName)) {
                throw new IllegalArgumentException("There's as an infinite loop in your events! The following events are involved: " + banned);
            }
            Map<Object, ? extends ConfigurationNode> eventData = customEvents.get(eventName).childrenMap();
            int alcohol = readWithDefault(eventData, "alcohol", ConfigurationNode::getInt, 0);
            int toxin = readWithDefault(eventData, "toxins", ConfigurationNode::getInt, 0);
            int probabilityWeight = readWithDefault(eventData, "probability-weight", ConfigurationNode::getInt, 0);

            List<? extends Map<Object, ? extends ConfigurationNode>> rawSteps = EventRegistrySerializer.<List<ConfigurationNode>>readWithDefault(eventData, "steps", configurationNode ->
                                    configurationNode.getList(ConfigurationNode.class)
                            , List.of()).stream()
                    .map(ConfigurationNode::childrenMap)
                    .toList();

            List<EventStep> eventSteps = new ArrayList<>();
            for (Map<Object, ? extends ConfigurationNode> step : rawSteps) {
                eventSteps.addAll(readEventStep(step, customEvents, banned, eventName));
            }

            return new CustomEvent(eventSteps, alcohol, toxin, probabilityWeight, eventName, BreweryKey.parse(eventName));
        } catch (IllegalArgumentException e) {
            Logging.error("Exception when reading custom event: " + eventName);
            throw new SerializationException(e);
        }
    }

    private List<EventStep> readEventStep(Map<Object, ? extends ConfigurationNode> step, Map<Object, ? extends ConfigurationNode> customEvents, Set<String> banned, String eventName) throws SerializationException {
        Preconditions.checkArgument(step.containsKey("type"), "Step has to have a type");
        String stepType = step.get("type").getString();
        Preconditions.checkArgument(stepType != null, "Step has to have a type");
        NamedDrunkEvent namedDrunkEvent = Registry.DRUNK_EVENT.get(BreweryKey.parse(stepType));
        if (namedDrunkEvent != null) {
            return List.of(getChildImpl(namedDrunkEvent.getClass()));
        } else if (stepType.equals("event")) {
            Preconditions.checkArgument(step.containsKey("event"), "Event step has to have a defined event");
            return List.of(readEvent(customEvents, step.get("event").getString(), Stream.concat(banned.stream(), Stream.of(eventName)).collect(Collectors.toSet())));
        } else if (stepType.equals("command")) {
            String commandSender = readWithDefault(step, "as", ConfigurationNode::getString, "server");
            SendCommand.CommandSenderType senderType = SendCommand.CommandSenderType.valueOf(commandSender.toUpperCase(Locale.ROOT));
            Preconditions.checkArgument(step.containsKey("command"), "Command can not be null in event step for event: " + eventName);
            String command = step.get("command").getString();
            Preconditions.checkArgument(command != null, "Command can not be empty");
            Preconditions.checkNotNull(command, "Command can not be null in event step for event: " + eventName);
            return List.of(getChildImpl(SendCommand.class, command, senderType));
        } else if (stepType.equals("wait")) {
            List<EventStep> output = new ArrayList<>();
            if (step.containsKey("condition")) {
                String condition = step.get("condition").getString();
                Preconditions.checkArgument(condition != null, "Condition can not be empty");
                output.add(getChildImpl(ConditionalWaitStep.class, condition));
            }
            if (step.containsKey("duration")) {
                String duration = step.get("duration").getString();
                Preconditions.checkArgument(duration != null, "Duration can not be empty");
                output.add(getChildImpl(WaitStep.class, duration));
            }
            Preconditions.checkArgument(step.containsKey("duration") || step.containsKey("condition"), "Expected duration or condition to be specified");
            return output;
        } else if (stepType.equals("potion")) {
            Preconditions.checkArgument(step.containsKey("effect"));
            String effect = step.get("effect").getString();
            Preconditions.checkArgument(effect != null, "Effect can not be empty");
            Interval amplifier = readWithDefault(step, "amplifier", node -> node.get(Interval.class), new Interval(1, 1));
            Interval duration = readWithDefault(step, "duration", node -> node.get(Interval.class), new Interval(10 * Moment.SECOND, 10 * Moment.SECOND));
            return List.of(getChildImpl(ApplyPotionEffect.class, effect, amplifier, duration));
        } else if (stepType.equals("consume")) {
            int alcohol = 0;
            if (step.containsKey("alcohol")) {
                alcohol = step.get("alcohol").getInt();
            }
            int toxins = 0;
            if (step.containsKey("toxins")) {
                toxins = step.get("toxins").getInt();
            }
            return List.of(getChildImpl(ConsumeStep.class, alcohol, toxins));
        } else if (stepType.equals("teleport")) {
            Preconditions.checkArgument(step.containsKey("location"), "Expected a location");
            Supplier<BreweryLocation> breweryLocation = step.get("location").get(new TypeToken<>() {
            });
            Preconditions.checkArgument(breweryLocation != null, "Expected a non empty location");
            return List.of(getChildImpl(Teleport.class, breweryLocation));
        }
        throw new IllegalArgumentException("Unknown step type");
    }

    private Map<String, Object> stepToConfigSerializable(EventStep step) {
        return switch (step) {
            case ApplyPotionEffect applyPotionEffect -> Map.of(
                    "type", "potion",
                    "effect", applyPotionEffect.getPotionEffectName(),
                    "duration", applyPotionEffect.getDurationBounds().asString(),
                    "amplifier", applyPotionEffect.getAmplifierBounds().asString()
            );
            case ConditionalWaitStep conditionalWaitStep -> Map.of(
                    "type", "wait",
                    "condition", conditionalWaitStep.getCondition().toString().toLowerCase(Locale.ROOT)
            );
            case ConsumeStep consumeStep -> Map.of(
                    "type", "consume",
                    "alcohol", consumeStep.getAlcohol(),
                    "toxins", consumeStep.getToxins()
            );
            case CustomEvent customEvent -> Map.of(
                    "type", "event",
                    "event", customEvent.key().key()
            );
            case NamedDrunkEvent namedDrunkEvent -> Map.of(
                    "type", namedDrunkEvent.key().key()
            );
            case SendCommand sendCommand -> Map.of(
                    "type", "command",
                    "as", sendCommand.getSenderType().toString().toLowerCase(Locale.ROOT),
                    "command", sendCommand.getCommand()
            );
            case Teleport teleport -> Map.of(
                    "type", "teleport",
                    "location", teleport.getLocation()
            );
            case WaitStep waitStep -> Map.of("type", "wait",
                    "duration", waitStep.durationTicks() + "t");
            default -> throw new IllegalArgumentException("Unsupported event step: " + step);
        };
    }

    private interface FunctionThatThrows<T, U, E extends Throwable> {
        U apply(T t) throws E;
    }

    public record ConstructorKey(Class<? extends EventStep> parent, List<Class<?>> paramTypes) {
        // This is used to uniquely identify a constructor for a specific EventStep class
    }

    protected static ConstructorKey key(Class<? extends EventStep> cls, Class<?>... params) {
        return new ConstructorKey(cls, List.of(params));
    }

    public abstract <T extends EventStep> T getChildImpl(Class<? extends EventStep> parent, Object... arguments);
}
