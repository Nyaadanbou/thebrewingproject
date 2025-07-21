package dev.jsinco.brewery.event.step;


import dev.jsinco.brewery.event.EventStep;

public record SendCommand(String command, CommandSenderType senderType) implements EventStep {

    public enum CommandSenderType {
        PLAYER,
        SERVER
    }
}