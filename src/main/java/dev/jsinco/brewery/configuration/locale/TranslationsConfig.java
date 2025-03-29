package dev.jsinco.brewery.configuration.locale;

import dev.jsinco.brewery.configuration.AbstractConfig;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.util.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TranslationsConfig extends AbstractConfig {

    @Key("brew.tooltip-ingredients")
    public static String BREW_TOOLTIP_INGREDIENTS;

    @Key("brew.tooltip-aging")
    public static String BREW_TOOLTIP_AGING;

    @Key("brew.tooltip-distilling")
    public static String BREW_TOOLTIP_DISTILLING;

    @Key("brew.tooltip-cooking")
    public static String BREW_TOOLTIP_COOKING;

    @Key("brew.tooltip-quality")
    public static String BREW_TOOLTIP_QUALITY;

    @Key("brew.tooltip-sealed")
    public static List<String> BREW_TOOLTIP_SEALED;

    @Key("distillery.create")
    public static String DISTILLERY_CREATE;

    @Key("command.create.unique-argument")
    public static String COMMAND_CREATE_UNIQUE_ARGUMENT;

    @Key("command.create.unknown-argument")
    public static String COMMAND_CREATE_UNKNOWN_ARGUMENT;

    @Key("command.create.missing-mandatory-argument")
    public static String COMMAND_CREATE_MISSING_MANDATORY_ARGUMENT;

    private static final TranslationsConfig TRANSLATIONS = new TranslationsConfig();

    public static void reload(File dataFolder) {
        Path mainDir = dataFolder.toPath();

        // extract default config from jar
        String fileName = "locale/" + Config.LANGUAGE + ".yml";
        FileUtil.extractFile(TranslationsConfig.class, fileName, mainDir, false);

        TRANSLATIONS.reload(mainDir.resolve(fileName), TranslationsConfig.class);
    }
}
