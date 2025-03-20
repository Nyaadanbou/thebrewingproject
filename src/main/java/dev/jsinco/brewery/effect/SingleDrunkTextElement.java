package dev.jsinco.brewery.effect;

import java.util.Random;

public record SingleDrunkTextElement(String text, int percentage, int alcohol) implements DrunkTextElement {
    private static final Random RANDOM = new Random();


    @Override
    public String transform(String initial) {
        if (percentage < RANDOM.nextInt(0, 101)) {
            return initial;
        }
        int randomPos = RANDOM.nextInt(0, initial.length() + 1);
        return initial.substring(0, randomPos) + text + initial.substring(randomPos);
    }
}
