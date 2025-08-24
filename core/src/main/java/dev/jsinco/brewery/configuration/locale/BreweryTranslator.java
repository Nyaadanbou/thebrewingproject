package dev.jsinco.brewery.configuration.locale;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.configuration.Config;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class BreweryTranslator extends MiniMessageTranslator {

    Map<Locale, Properties> translations;
    File localeDirectory;

    public BreweryTranslator(File localeDirectory) {
        this.localeDirectory = localeDirectory;
        syncLangFiles();
        loadLangFiles();
    }

    public void syncLangFiles() {
        if (!localeDirectory.exists() && !localeDirectory.mkdirs()) { // shouldn't happen
            throw new IllegalStateException("Failed to create locale directory at " + localeDirectory.getAbsolutePath());
        }

        try {
            // load language files from inside the jar (can't use main class here, sadly)
            Enumeration<URL> resources = getClass().getClassLoader().getResources("locale");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                try (FileSystem fs = "jar".equals(url.getProtocol()) ? FileSystems.newFileSystem(url.toURI(), Collections.emptyMap()) : null) {

                    Path internalLocaleDir = fs == null ? Paths.get(url.toURI()) : fs.getPath("locale");
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(internalLocaleDir, "*.lang")) {

                        for (Path internalFile : stream) {

                            String fileName = internalFile.getFileName().toString();
                            File externalFile = new File(localeDirectory, fileName);

                            // load the internal/default properties from within the jar
                            Properties internalProps = new Properties();
                            try (Reader reader = Files.newBufferedReader(internalFile, StandardCharsets.UTF_8)) {
                                internalProps.load(reader);
                            }

                            Properties merged = new Properties();
                            if (externalFile.exists()) {

                                // load external file if it already exists
                                Properties externalProps = new Properties();
                                try (Reader reader = new InputStreamReader(new FileInputStream(externalFile), StandardCharsets.UTF_8)) {
                                    externalProps.load(reader);
                                }

                                // keep set values, but add new ones from the internal file
                                for (String key : internalProps.stringPropertyNames()) {
                                    merged.setProperty(key, externalProps.getProperty(key, internalProps.getProperty(key)));
                                }

                            } else {
                                merged.putAll(internalProps);
                            }

                            // save merged file
                            try (Writer writer = new OutputStreamWriter(new FileOutputStream(externalFile), StandardCharsets.UTF_8)) {
                                storeWithoutComments(merged, writer);
                            }
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to sync language files", e);
        }
        // special thanks to StackOverflow and other useful sites lol
    }

    // Properties#store would automatically add the current date as a comment at the top of the file
    private void storeWithoutComments(Properties props, Writer writer) throws IOException {

        List<String> keys = new ArrayList<>(props.stringPropertyNames());
        Collections.sort(keys); // alphabetically sort entries for consistency

        for (String key : keys) {
            writer.write(key + "=" + props.getProperty(key) + "\n");
        }
    }

    public void loadLangFiles() {
        if (!localeDirectory.isDirectory()) {
            throw new IllegalArgumentException("Locale directory is not a directory!");
        }
        ImmutableMap.Builder<Locale, Properties> translationsBuilder = new ImmutableMap.Builder<>();
        for (File translationFile : localeDirectory.listFiles(file -> file.getName().endsWith(".lang"))) {
            try (InputStream inputStream = new FileInputStream(translationFile)) {
                Properties translation = new Properties();
                translation.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                translationsBuilder.put(Locale.forLanguageTag(translationFile.getName().replaceAll(".lang$", "")), translation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.translations = translationsBuilder.build();
        Preconditions.checkArgument(translations.containsKey(Config.config().language()), "Unknown translation: " + Config.config().language());
    }

    @Override
    public @NotNull Key name() {
        return Key.key("brewery:global_translator");
    }

    @Override
    public @Nullable String getMiniMessageString(@NotNull String key, @NotNull Locale locale) {
        Properties translations = null;
        if (Config.config().clientSideTranslations()) {
            translations = this.translations.get(locale);
        }
        if (translations == null) {
            translations = this.translations.get(Config.config().language());
        }
        Preconditions.checkState(translations != null, "Should have found a translation!");
        return translations.getProperty(key);
    }
}
