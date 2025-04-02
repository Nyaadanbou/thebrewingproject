package dev.jsinco.brewery.effect.event;

public sealed interface EventStep permits ApplyPotionEffect, SendCommand, Teleport, DrunkEvent, WaitStep {
}
