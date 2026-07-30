package org.kvxd.fasterchests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FasterChestsConfig {

    public static final boolean STATIC_CHESTS;

    private static final Path FILE = Path.of("config", "fasterchests.properties");
    private static final String KEY = "staticChests";

    static {
        boolean value = true;
        try {
            Properties properties = new Properties();
            if (Files.exists(FILE)) {
                try (var in = Files.newInputStream(FILE)) {
                    properties.load(in);
                }
                value = !"false".equalsIgnoreCase(properties.getProperty(KEY, "true").trim());
            } else {
                Files.createDirectories(FILE.getParent());
                properties.setProperty(KEY, "true");
                try (var out = Files.newOutputStream(FILE)) {
                    properties.store(out, "staticChests=false restores the vanilla animated chest lid");
                }
            }
        } catch (IOException ignored) {
        }
        STATIC_CHESTS = value;
    }

    private FasterChestsConfig() {
    }
}
