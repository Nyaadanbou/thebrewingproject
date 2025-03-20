package dev.jsinco.brewery.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleDrunkTextElementTest {


    @Test
    void transform_empty() {
        SingleDrunkTextElement singleDrunkTextElement = new SingleDrunkTextElement("*hic*", 100, 0);
        assertEquals("*hic*", singleDrunkTextElement.transform(""));
    }

    @Test
    void transform() {
        SingleDrunkTextElement singleDrunkTextElement = new SingleDrunkTextElement("*hic*", 100, 0);
        String transformed = singleDrunkTextElement.transform(" ");
        assertTrue(transformed.equals("*hic* ") || transformed.equals(" *hic*"), "Did not insert any drunk text");
    }
}