package dev.jsinco.brewery.api.event.step;

public sealed interface Condition {

    record JoinedWorld(String worldName) implements Condition {
    }

    record JoinedServer() implements Condition {
    }

    record TookDamage() implements Condition {
    }

    record Died() implements Condition {
    }

    record HasPermission(String permission) implements Condition {
    }

    record ModifierAbove(String modifier, double value) implements Condition {
    }

    record NotCondition(Condition toInvert) implements Condition {
    }
}
