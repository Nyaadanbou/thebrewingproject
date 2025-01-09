package dev.jsinco.brewery.util;

import java.io.*;

public class DecoderEncoder {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    private static final byte LIST_ENCODING_VERSION = 0; // In case something needs to change and this plugin has been published

    private DecoderEncoder() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] encode(byte[][] byteArrayArray) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        arrayOutputStream.write(LIST_ENCODING_VERSION);
        writeVarInt(byteArrayArray.length, arrayOutputStream);
        for (byte[] byteArray : byteArrayArray) {
            writeVarInt(byteArray.length, arrayOutputStream);
            arrayOutputStream.write(byteArray);
        }
        return arrayOutputStream.toByteArray();
    }

    public static byte[][] decode(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        byte version = (byte) byteArrayInputStream.read();
        if (version != LIST_ENCODING_VERSION) {
            throw new IOException("Invalid format");
        }
        int listSize = readVarInt(byteArrayInputStream);
        byte[][] output = new byte[listSize][];
        for (int i = 0; i < listSize; i++) {
            int stringLength = readVarInt(byteArrayInputStream);
            output[i] = byteArrayInputStream.readNBytes(stringLength);
        }
        return output;
    }

    public static int readVarInt(InputStream inputStream) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = (byte) inputStream.read();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new IOException("VarInt is too big");
        }
        return value;
    }

    public static void writeVarInt(int value, OutputStream outputStream) throws IOException {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                outputStream.write(value);
                return;
            }

            outputStream.write((value & SEGMENT_BITS) | CONTINUE_BIT);

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
        }
    }
}
