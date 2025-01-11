package dev.jsinco.brewery.util;

import org.joml.Matrix3d;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecoderEncoderTest {

    @Test
    void encodeDecode() throws IOException {
        byte[][] toEncode = List.of("Hello", "world", "!").stream()
                .map(string -> string.getBytes(StandardCharsets.UTF_8))
                .toArray(byte[][]::new);
        byte[] encoded = DecoderEncoder.encode(toEncode);
        assertTrue(Arrays.deepEquals(toEncode, DecoderEncoder.decode(encoded)));
    }

    @Test
    void writeReadVarInt() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int expected = 42;
        DecoderEncoder.writeVarInt(expected, outputStream);
        assertEquals(expected, DecoderEncoder.readVarInt(new ByteArrayInputStream(outputStream.toByteArray())));
    }

    @Test
    void serializeDeserializeMatrix3d(){
        Matrix3d initial = new Matrix3d(1d, 4d, 3d, 5d, 6d, 7d, 2d, 3d, 9d);
        Matrix3d deserialized = DecoderEncoder.deserializeTransformation(DecoderEncoder.serializeTransformation(initial));
        assertEquals(initial, deserialized);
    }
}