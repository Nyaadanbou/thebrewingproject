package dev.jsinco.brewery.event;

public class IllegalEventStepCall extends RuntimeException {

    public IllegalEventStepCall() {
        super("This EventStep method cannot be called directly. Use the EventStepRegistry to upgrade this class to an executable implementation.");
    }

    public IllegalEventStepCall(String message) {
        super(message);
    }
}
