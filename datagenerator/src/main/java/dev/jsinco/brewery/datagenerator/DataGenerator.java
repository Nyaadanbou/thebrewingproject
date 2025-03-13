package dev.jsinco.brewery.datagenerator;

import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

public class DataGenerator {

    public static void main(String[] args) throws URISyntaxException, IOException {
        if (args.length != 1) {
            System.out.print("Usage: <target folder>");
            return;
        }
        File outputFolder = new File(args[0]);
        URL url = ClassLoader.getSystemResource("assets/minecraft/textures/item/apple.png");
        URI uri = url.toURI();
        try {
            generateColorData(Paths.get(uri), outputFolder);
        } catch (FileSystemNotFoundException e) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                generateColorData(Paths.get(uri), outputFolder);
            }
        }
    }

    private static void generateColorData(Path path, File outputFolder) throws IOException {
        Path directory = path.getParent();
        JsonObject jsonObject = new JsonObject();
        try (Stream<Path> walk = Files.walk(directory)) {
            Iterator<Path> walkIterator = walk.iterator();
            while (walkIterator.hasNext()) {
                Path next = walkIterator.next();
                if (!next.toString().endsWith(".png")) {
                    continue;
                }
                try (InputStream inputStream = Files.newInputStream(next)) {
                    BufferedImage image = ImageIO.read(inputStream);
                    Color color = ColorUtil.getAverageColor(image);
                    jsonObject.addProperty(next.getFileName().toString().replace(".png", ""), Integer.toHexString(color.getRGB() & 0x00ffffff));
                }
            }
        }
        JsonUtil.dump(jsonObject, new File(outputFolder, "colors.json"));
    }
}
