package dev.jsinco.brewery.effect.event;

public sealed interface EventStep permits ApplyPotionEffect, ConditionalWaitStep, ConsumeStep, DrunkEvent, SendCommand, Teleport, WaitStep {
}
