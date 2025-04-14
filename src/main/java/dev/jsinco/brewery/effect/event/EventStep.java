package dev.jsinco.brewery.effect.event;

public sealed interface EventStep permits ApplyPotionEffect, ConditionalWaitStep, DrunkEvent, SendCommand, Teleport, WaitStep {
}
