package dev.jsinco.brewery.structure;

public class StructureReadException extends Exception {

    public StructureReadException(String message) {
        super(message);
    }

    public StructureReadException(Throwable throwable) {
        super(throwable);
    }

    public StructureReadException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
