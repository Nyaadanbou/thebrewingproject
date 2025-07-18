package dev.jsinco.brewery.event.step;


import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

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

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        throw new IllegalEventStepCall();
    }

    @Override
    public void register(EventStepRegistry registry) {
        throw new IllegalEventStepCall();
    }
}