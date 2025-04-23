package dev.jsinco.brewery.event;

public sealed interface EventStep permits ApplyPotionEffect, ConditionalWaitStep, ConsumeStep, DrunkEvent, SendCommand, Teleport, WaitStep {
}
