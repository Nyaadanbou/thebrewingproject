package dev.jsinco.brewery.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {


    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    public static final Map<String, Color> NAME_TO_COLOR_MAP = new HashMap<>();

    static {
        NAME_TO_COLOR_MAP.put("WHITE", Color.WHITE);
        NAME_TO_COLOR_MAP.put("SILVER", Color.SILVER);
        NAME_TO_COLOR_MAP.put("GRAY", Color.GRAY);
        NAME_TO_COLOR_MAP.put("BLACK", Color.BLACK);
        NAME_TO_COLOR_MAP.put("RED", Color.RED);
        NAME_TO_COLOR_MAP.put("MAROON", Color.MAROON);
        NAME_TO_COLOR_MAP.put("YELLOW", Color.YELLOW);
        NAME_TO_COLOR_MAP.put("OLIVE", Color.OLIVE);
        NAME_TO_COLOR_MAP.put("LIME", Color.LIME);
        NAME_TO_COLOR_MAP.put("GREEN", Color.GREEN);
        NAME_TO_COLOR_MAP.put("AQUA", Color.AQUA);
        NAME_TO_COLOR_MAP.put("TEAL", Color.TEAL);
        NAME_TO_COLOR_MAP.put("BLUE", Color.BLUE);
        NAME_TO_COLOR_MAP.put("NAVY", Color.NAVY);
        NAME_TO_COLOR_MAP.put("FUCHSIA", Color.FUCHSIA);
        NAME_TO_COLOR_MAP.put("PURPLE", Color.PURPLE);
        NAME_TO_COLOR_MAP.put("ORANGE", Color.ORANGE);
        NAME_TO_COLOR_MAP.put("PINK", Color.FUCHSIA);
        NAME_TO_COLOR_MAP.put("BRIGHT_GRAY", Color.SILVER);
        NAME_TO_COLOR_MAP.put("BRIGHT_RED", Color.fromRGB(255, 0, 0));
        NAME_TO_COLOR_MAP.put("DARK_RED", Color.fromRGB(128, 0, 0));
    }


    public static Color parseColorString(String hexOrValue) {
        hexOrValue = hexOrValue.replace("&", "").replace("#", "").toUpperCase();
        if (NAME_TO_COLOR_MAP.containsKey(hexOrValue)) {
            return NAME_TO_COLOR_MAP.get(hexOrValue);
        }
        try {
            return Color.fromRGB(
                    Integer.valueOf(hexOrValue.substring(0, 2), 16),
                    Integer.valueOf(hexOrValue.substring(2, 4), 16),
                    Integer.valueOf(hexOrValue.substring(4, 6), 16));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid color string: " + hexOrValue);
        }
    }

    public static String colorText(String msg) {
        String[] texts = msg.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                //get the next string
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }

    // Returns a color closer to the destination color based on the interval and totalDuration
    public static Color getNextColor(Color current, Color destination, long step, int duration) {
        float ratio = Math.min((float) step / (duration - 1), 1f);
        int red = (int) (ratio * destination.getRed() + (1f - ratio) * current.getRed());
        int green = (int) (ratio * destination.getGreen() + (1f - ratio) * current.getGreen());
        int blue = (int) (ratio * destination.getBlue() + (1f - ratio) * current.getBlue());

        return Color.fromRGB(red, green, blue);
    }
}
