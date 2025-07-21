package dev.jsinco.brewery.event.step;


import dev.jsinco.brewery.event.EventStep;

public class SendCommand implements EventStep {

    private final String command;
    private final CommandSenderType senderType;

    public SendCommand(String command, CommandSenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

    public String getCommand() {
        return command;
    }

    public CommandSenderType getSenderType() {
        return senderType;
    }

    public enum CommandSenderType {
        PLAYER,
        SERVER
    }
}