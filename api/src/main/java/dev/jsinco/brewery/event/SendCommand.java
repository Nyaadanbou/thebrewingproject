package dev.jsinco.brewery.event;

public record SendCommand(String command, CommandSenderType senderType) implements EventStep {

    public enum CommandSenderType {
        PLAYER,
        SERVER
    }
}
