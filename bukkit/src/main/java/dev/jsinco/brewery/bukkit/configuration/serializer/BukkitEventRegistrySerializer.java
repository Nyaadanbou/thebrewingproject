package dev.jsinco.brewery.bukkit.configuration.serializer;

import dev.jsinco.brewery.bukkit.effect.named.ChickenNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.DrunkMessageNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.DrunkenWalkNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.NauseaNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.PassOutNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.PukeNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.StumbleNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.named.TeleportNamedDrunkEventImpl;
import dev.jsinco.brewery.bukkit.effect.step.ApplyPotionEffectImpl;
import dev.jsinco.brewery.bukkit.effect.step.ConditionalWaitStepImpl;
import dev.jsinco.brewery.bukkit.effect.step.ConsumeStepImpl;
import dev.jsinco.brewery.bukkit.effect.step.SendCommandImpl;
import dev.jsinco.brewery.bukkit.effect.step.TeleportImpl;
import dev.jsinco.brewery.bukkit.effect.step.WaitStepImpl;
import dev.jsinco.brewery.configuration.serializers.EventRegistrySerializer;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.named.ChickenNamedDrunkEvent;
import dev.jsinco.brewery.event.named.DrunkMessageNamedDrunkEvent;
import dev.jsinco.brewery.event.named.DrunkenWalkNamedDrunkEvent;
import dev.jsinco.brewery.event.named.NauseaNamedDrunkEvent;
import dev.jsinco.brewery.event.named.PassOutNamedDrunkEvent;
import dev.jsinco.brewery.event.named.PukeNamedDrunkEvent;
import dev.jsinco.brewery.event.named.StumbleNamedDrunkEvent;
import dev.jsinco.brewery.event.named.TeleportNamedDrunkEvent;
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.step.ConsumeStep;
import dev.jsinco.brewery.event.step.SendCommand;
import dev.jsinco.brewery.event.step.Teleport;
import dev.jsinco.brewery.event.step.WaitStep;
import dev.jsinco.brewery.moment.Interval;
import dev.jsinco.brewery.vector.BreweryLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class BukkitEventRegistrySerializer extends EventRegistrySerializer {


    // I have to wrap these in entry() to have an infinite Map.of() method
    private static final Map<ConstructorKey, Function<Object[], EventStep>> FACTORY_MAP = Map.ofEntries(
            entry(key(SendCommand.class, String.class, SendCommand.CommandSenderType.class), args -> new SendCommandImpl((String) args[0], (SendCommand.CommandSenderType) args[1])),

            entry(key(Teleport.class, Supplier.class), args -> new TeleportImpl((Supplier<BreweryLocation>) args[0])),

            entry(key(WaitStep.class, Integer.class), args -> new WaitStepImpl((Integer) args[0])),
            entry(key(WaitStep.class, String.class), args -> new WaitStepImpl((String) args[0])),

            entry(key(ApplyPotionEffect.class, String.class, Interval.class, Interval.class), args -> new ApplyPotionEffectImpl((String) args[0], (Interval) args[1], (Interval) args[2])),

            entry(key(ConditionalWaitStep.class, ConditionalWaitStep.Condition.class), args -> new ConditionalWaitStepImpl((ConditionalWaitStep.Condition) args[0])),
            entry(key(ConditionalWaitStep.class, String.class), args -> new ConditionalWaitStepImpl((String) args[0])),

            entry(key(ConsumeStep.class, Integer.class, Integer.class), args -> new ConsumeStepImpl((Integer) args[0], (Integer) args[1])),

            entry(key(PukeNamedDrunkEvent.class), args -> new PukeNamedDrunkEventImpl()),
            entry(key(PassOutNamedDrunkEvent.class), args -> new PassOutNamedDrunkEventImpl()),
            entry(key(StumbleNamedDrunkEvent.class), args -> new StumbleNamedDrunkEventImpl()),
            entry(key(ChickenNamedDrunkEvent.class), args -> new ChickenNamedDrunkEventImpl()),
            entry(key(TeleportNamedDrunkEvent.class), args -> new TeleportNamedDrunkEventImpl()),
            entry(key(DrunkMessageNamedDrunkEvent.class), args -> new DrunkMessageNamedDrunkEventImpl()),
            entry(key(NauseaNamedDrunkEvent.class), args -> new NauseaNamedDrunkEventImpl()),
            entry(key(DrunkenWalkNamedDrunkEvent.class), args -> new DrunkenWalkNamedDrunkEventImpl())
    );


    @Override
    public <T extends EventStep> T getChildImpl(Class<? extends EventStep> parent, Object... arguments) {
        List<Class<?>> paramTypes = Arrays.stream(arguments)
                .map(arg -> arg != null ? arg.getClass() : Object.class) // crude null handling
                .toList();

        Function<Object[], EventStep> factory = FACTORY_MAP.get(new ConstructorKey(parent, paramTypes));
        if (factory != null) {
            return (T) factory.apply(arguments);
        }

        throw new IllegalArgumentException("No factory found for class: " + parent.getName() +
                " with parameters: " + paramTypes);
    }


    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return Map.entry(key, value);
    }

}
