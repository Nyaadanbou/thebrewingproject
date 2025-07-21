package dev.jsinco.brewery.bukkit.effect.event;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.effect.named.ChickenNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.DrunkMessageNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.DrunkenWalkNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.FeverNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.HallucinationNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.KaboomNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.NauseaNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.PassOutNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.PukeNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.StumbleNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.named.TeleportNamedExecutable;
import dev.jsinco.brewery.bukkit.effect.step.ApplyPotionEffectExecutable;
import dev.jsinco.brewery.bukkit.effect.step.ConditionalWaitStepExecutable;
import dev.jsinco.brewery.bukkit.effect.step.ConsumeStepExecutable;
import dev.jsinco.brewery.bukkit.effect.step.CustomEventExecutable;
import dev.jsinco.brewery.bukkit.effect.step.SendCommandExecutable;
import dev.jsinco.brewery.bukkit.effect.step.TeleportExecutable;
import dev.jsinco.brewery.bukkit.effect.step.WaitStepExecutable;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.NamedDrunkEvent;
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.step.ConsumeStep;
import dev.jsinco.brewery.event.step.CustomEvent;
import dev.jsinco.brewery.event.step.SendCommand;
import dev.jsinco.brewery.event.step.Teleport;
import dev.jsinco.brewery.event.step.WaitStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DrunkEventExecutor {

    private final Map<UUID, List<EventStep>> onJoinExecutions = new HashMap<>();

    public DrunkEventExecutor() {
        EventStepRegistry registry = TheBrewingProject.getInstance().getEventStepRegistry();
        registry.register(NamedDrunkEvent.fromKey("chicken"), ChickenNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("drunken_walk"), DrunkenWalkNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("drunk_message"), DrunkMessageNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("fever"), FeverNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("hallucination"), HallucinationNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("kaboom"), KaboomNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("nausea"), NauseaNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("pass_out"), PassOutNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("puke"), PukeNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("stumble"), StumbleNamedExecutable::new);
        registry.register(NamedDrunkEvent.fromKey("teleport"), TeleportNamedExecutable::new);
        registry.register(ApplyPotionEffect.class, o -> {
            ApplyPotionEffect e = (ApplyPotionEffect) o;
            return new ApplyPotionEffectExecutable(e.potionEffectName(), e.amplifierBounds(), e.durationBounds());
        });
        registry.register(ConditionalWaitStep.class, o -> {
            ConditionalWaitStep e = (ConditionalWaitStep) o;
            return new ConditionalWaitStepExecutable(e.getCondition());
        });
        registry.register(ConsumeStep.class, o -> {
            ConsumeStep e = (ConsumeStep) o;
            return new ConsumeStepExecutable(e.alcohol(), e.toxins());
        });
        registry.register(SendCommand.class, o -> {
            SendCommand e = (SendCommand) o;
            return new SendCommandExecutable(e.command(), e.senderType());
        });
        registry.register(Teleport.class, o -> {
            Teleport e = (Teleport) o;
            return new TeleportExecutable(e.location());
        });
        registry.register(WaitStep.class, o -> {
            WaitStep e = (WaitStep) o;
            return new WaitStepExecutable(e.durationTicks());
        });
        registry.register(CustomEvent.class, o -> {
            CustomEvent e = (CustomEvent) o;
            return new CustomEventExecutable(e.getSteps());
        });
    }

    public void doDrunkEvent(UUID playerUuid, EventStep event) {
        doDrunkEvents(playerUuid, List.of(event));
    }

    public void doDrunkEvents(UUID playerUuid, List<EventStep> events) {
        EventStepRegistry registry = TheBrewingProject.getInstance().getEventStepRegistry();

        for (int i = 0; i < events.size(); i++) {
            final EventStep event = events.get(i);

            ExecutableEventStep executableEventStep = registry.upgrade(event);
            if (executableEventStep == null) {
                throw new IllegalStateException("No ExecutableEventStep found for EventStep: " + event.getClass().getName());
            }
            executableEventStep.execute(playerUuid, events, i);
        }
    }

    public void onPlayerJoin(UUID playerUuid) {
        List<EventStep> eventSteps = onJoinExecutions.get(playerUuid);
        if (eventSteps == null) {
            return;
        }
        doDrunkEvents(playerUuid, eventSteps);
    }

    public void add(UUID playerUuid, List<EventStep> events) {
        onJoinExecutions.put(playerUuid, events);
    }

    public void clear(UUID playerUuid) {
        onJoinExecutions.remove(playerUuid);
    }

    public void clear() {
        onJoinExecutions.clear();
    }
}
