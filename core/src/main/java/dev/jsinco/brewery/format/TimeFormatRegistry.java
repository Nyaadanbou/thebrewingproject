package dev.jsinco.brewery.format;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class TimeFormatRegistry {

    private static final Map<TimeFormat, String> timeFormats = new EnumMap<>(TimeFormat.class);

    public void sync(File externalFile) throws IOException {
        InputStream internalDefaultsStream = getClass().getClassLoader()
                .getResourceAsStream("locale/" + externalFile.getName());
        if (internalDefaultsStream == null) return; // not present internally
        Properties internal = loadProperties(internalDefaultsStream);
        sync(externalFile, internal);
    }

    public void sync(File externalFile, Properties internalDefaults) throws IOException {

        Objects.requireNonNull(internalDefaults, "internalDefaults");
        Objects.requireNonNull(externalFile, "externalFile");

        Properties external = loadExternalIfPresent(externalFile);

        Properties merged = new Properties();
        for (String key : internalDefaults.stringPropertyNames()) {
            String val = external.getProperty(key, internalDefaults.getProperty(key));
            merged.setProperty(key, val);
        }

        writeExternal(externalFile, merged);
    }

    public void load(File externalFile) throws IOException {
        InputStream internalDefaultsStream = getClass().getClassLoader()
                .getResourceAsStream("locale/" + externalFile.getName());
        if (internalDefaultsStream == null) return; // not present internally
        Properties internal = loadProperties(internalDefaultsStream);
        load(externalFile, internal);
    }

    public void load(File externalFile, Properties internalDefaults) throws IOException {

        Objects.requireNonNull(internalDefaults, "internalDefaults");
        Objects.requireNonNull(externalFile, "externalFile");

        Properties external = loadExternalIfPresent(externalFile);

        timeFormats.clear();
        for (TimeFormat tf : TimeFormat.values()) {
            String key = tf.getKey();
            String value = firstNonEmpty(
                    external.getProperty(key),
                    internalDefaults.getProperty(key),
                    key // fallback: the key itself
            );
            timeFormats.put(tf, value);
        }
    }

    public void clear() {
        timeFormats.clear();
    }

    public static String get(TimeFormat format) {
        return timeFormats.getOrDefault(format, format.getKey());
    }

    private static String firstNonEmpty(String a, String b, String c) {
        if (a != null && !a.isEmpty()) return a;
        if (b != null && !b.isEmpty()) return b;
        return c;
    }

    private static Properties loadProperties(InputStream in) throws IOException {
        try (InputStream is = in; Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Properties p = new Properties();
            p.load(reader);
            return p;
        }
    }

    private static Properties loadExternalIfPresent(File externalFile) throws IOException {
        Properties p = new Properties();
        if (externalFile.isFile()) {
            try (InputStream in = new FileInputStream(externalFile);
                 Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                p.load(reader);
            }
        }
        return p;
    }

    private static void writeExternal(File externalFile, Properties properties) throws IOException {
        File parent = externalFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory: " + parent.getAbsolutePath());
        }
        try (
            OutputStream out = new FileOutputStream(externalFile, false);
            Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)
        ) {
            writeExternal(properties, writer);
        }
    }

    // Properties#store would automatically add the current date as a comment at the top of the file
    private static void writeExternal(Properties props, Writer writer) throws IOException {

        writer.write(TimeFormatHeader.get()); // Potentially localized explanation

        List<String> keys = new ArrayList<>(props.stringPropertyNames());
        Collections.sort(keys); // alphabetically sort entries for consistency

        for (String key : keys) {
            writer.write(key + "=" + props.getProperty(key) + "\n");
        }
    }

}
