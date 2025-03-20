package dev.jsinco.brewery.effect;

public class DrunkTextTransformer {

    public static String transform(String text, DrunkTextRegistry registry, int alcohol) {
        String output = text;
        for (DrunkTextElement transformer : registry.getTextTransformers(alcohol)) {
            output = transformer.transform(output);
        }
        return output;
    }
}
