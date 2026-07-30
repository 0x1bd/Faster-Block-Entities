package org.kvxd.fasterblockentities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FBEConfig {

    public static final boolean STATIC_CHESTS;
    public static final boolean STATIC_SHULKER_BOXES;

    private static final Path FILE = Path.of("config", "fasterblockentities.properties");

    static {
        Properties properties = new Properties();
        boolean existed = Files.exists(FILE);
        if (existed) {
            try (var in = Files.newInputStream(FILE)) {
                properties.load(in);
            } catch (IOException ignored) {
            }
        }

        STATIC_CHESTS = read(properties, "staticChests", true);
        STATIC_SHULKER_BOXES = read(properties, "staticShulkerBoxes", false);

        if (!existed) {
            properties.setProperty("staticChests", Boolean.toString(STATIC_CHESTS));
            properties.setProperty("staticShulkerBoxes", Boolean.toString(STATIC_SHULKER_BOXES));
            try {
                Files.createDirectories(FILE.getParent());
                try (var out = Files.newOutputStream(FILE)) {
                    properties.store(out, "Set a key to false to restore that block entity's vanilla renderer and animation");
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean read(final Properties properties, final String key, final boolean fallback) {
        String value = properties.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value.trim());
    }

    private FBEConfig() {
    }
}
