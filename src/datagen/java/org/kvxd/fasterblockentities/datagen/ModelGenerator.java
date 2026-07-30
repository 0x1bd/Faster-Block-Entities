package org.kvxd.fasterblockentities.datagen;

import java.nio.file.Path;

public final class ModelGenerator {

    public static void main(final String[] args) {
        Assets assets = new Assets(Path.of(args[0]));
        ShulkerBoxes.generate(assets);
        System.exit(0);
    }

    private ModelGenerator() {
    }
}
