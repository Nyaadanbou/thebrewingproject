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
import dev.jsinco.brewery.bukkit.effect.step.ApplyPotionEffectImpl;
import dev.jsinco.brewery.bukkit.effect.step.CustomEventImpl;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.SendCommand;
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
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.event.step.ConditionalWaitStep;
import dev.jsinco.brewery.event.step.ConsumeStep;
import dev.jsinco.brewery.event.step.CustomEvent;
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
        registry.register(ChickenNamedEvent.class, o -> new ChickenNamedExecutable());
        registry.register(DrunkenWalkNamedEvent.class, o -> new DrunkenWalkNamedExecutable());
        registry.register(DrunkMessageNamedEvent.class, o -> new DrunkMessageNamedExecutable());
        registry.register(FeverNamedEvent.class, o -> new FeverNamedExecutable());
        registry.register(HallucinationNamedEvent.class, o -> new HallucinationNamedExecutable());
        registry.register(KaboomNamedEvent.class, o -> new KaboomNamedExecutable());
        registry.register(NauseaNamedEvent.class, o -> new NauseaNamedExecutable());
        registry.register(PassOutNamedEvent.class, o -> new PassOutNamedExecutable());
        registry.register(PukeNamedEvent.class, o -> new PukeNamedExecutable());
        registry.register(StumbleNamedEvent.class, o -> new StumbleNamedExecutable());
        registry.register(TeleportNamedEvent.class, o -> new TeleportNamedExecutable());
        registry.register(ApplyPotionEffect.class, o -> {
            ApplyPotionEffect e = (ApplyPotionEffect) o;
            return new ApplyPotionEffectImpl(e.getPotionEffectName(), e.getAmplifierBounds(), e.getDurationBounds());
        });
        registry.register(ConditionalWaitStep.class, o -> {
            ConditionalWaitStep e = (ConditionalWaitStep) o;
            return new ConditionalWaitStep(e.getCondition());
        });
        registry.register(ConsumeStep.class, o -> {
            ConsumeStep e = (ConsumeStep) o;
            return new ConsumeStep(e.getAlcohol(), e.getToxins());
        });
        registry.register(SendCommand.class, o -> {
            SendCommand e = (SendCommand) o;
            return new SendCommand(e.getCommand(), e.getSenderType());
        });
        registry.register(Teleport.class, o -> {
            Teleport e = (Teleport) o;
            return new Teleport(e.getLocation());
        });
        registry.register(WaitStep.class, o -> {
            WaitStep e = (WaitStep) o;
            return new WaitStep(e.getDurationTicks());
        });
        registry.register(CustomEvent.class, o -> {
            CustomEvent e = (CustomEvent) o;
            return new CustomEventImpl(e.getSteps(), e.alcoholRequirement(), e.toxinsRequirement(), e.probabilityWeight(), e.displayName(), e.key());
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
