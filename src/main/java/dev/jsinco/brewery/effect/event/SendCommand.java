package dev.jsinco.brewery.effect.event;

public record SendCommand(String command, CommandSenderType senderType) implements EventStep {

    public enum CommandSenderType {
        PLAYER,
        SERVER
    }
}
