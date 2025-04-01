package dev.jsinco.brewery.configuration.locale;

import dev.jsinco.brewery.configuration.AbstractConfig;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.util.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    @Key("distillery.create-denied")
    public static String DISTILLERY_CREATE_DENIED;

    @Key("distillery.access-denied")
    public static String DISTILLERY_ACCESS_DENIED;

    @Key("barrel.create")
    public static String BARREL_CREATE;

    @Key("barrel.type.none")
    public static String BARREL_TYPE_NONE;

    @Key("barrel.type")
    public static Map<String, String> BARREL_TYPE;

    @Key("barrel.create-denied")
    public static String BARREL_CREATE_DENIED;

    @Key("barrel.access-denied")
    public static String BARREL_ACCESS_DENIED;

    @Key("cauldron.access-denied")
    public static String CAULDRON_ACCESS_DENIED;

    @Key("cauldron.type")
    public static Map<String, String> CAULDRON_TYPE;

    @Key("cauldron.type.none")
    public static String CAULDRON_TYPE_NONE;

    @Key("command.create.unique-argument")
    public static String COMMAND_CREATE_UNIQUE_ARGUMENT;

    @Key("command.create.unknown-argument")
    public static String COMMAND_CREATE_UNKNOWN_ARGUMENT;

    @Key("command.create.missing-mandatory-argument")
    public static String COMMAND_CREATE_MISSING_MANDATORY_ARGUMENT;

    @Key("command.status.info.message")
    public static String COMMAND_STATUS_INFO_MESSAGE;

    @Key("command.status.consume.message")
    public static String COMMAND_STATUS_CONSUME_MESSAGE;

    @Key("command.status.clear.message")
    public static String COMMAND_STATUS_CLEAR_MESSAGE;

    @Key("command.status.set.message")
    public static String COMMAND_STATUS_SET_MESSAGE;

    @Key("command.unknown-player")
    public static String COMMAND_UNKNOWN_PLAYER;

    @Key("command.not-enough-permissions")
    public static String COMMAND_NOT_ENOUGH_PERMISSIONS;

    @Key("command.info.not-a-brew")
    public static String COMMAND_INFO_NOT_A_BREW;

    @Key("command.info.message")
    public static String COMMAND_INFO_BREW_MESSAGE;

    @Key("command.info.effect-message")
    public static String COMMAND_INFO_EFFECT_MESSAGE;

    @Key("events.default-kick-event-message")
    public static String KICK_EVENT_MESSAGE;

    @Key("events.puke")
    public static String PUKE_EVENT;

    @Key("events.stumble")
    public static String STUMBLE_EVENT;

    @Key("events.kick")
    public static String KICK_EVENT;

    @Key("events.nothing-planned")
    public static String NO_EVENT_PLANNED;

    private static final TranslationsConfig TRANSLATIONS = new TranslationsConfig();

    public static void reload(File dataFolder) {
        Path mainDir = dataFolder.toPath();

        // extract default config from jar
        String fileName = "locale/" + Config.LANGUAGE + ".yml";
        FileUtil.extractFile(TranslationsConfig.class, fileName, mainDir, false);

        TRANSLATIONS.reload(mainDir.resolve(fileName), TranslationsConfig.class);
    }
}
