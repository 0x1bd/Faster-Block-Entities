package org.kvxd.fasterblockentities.datagen;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class Assets {

    private static final com.google.gson.Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path root;

    public Assets(final Path root) {
        this.root = root;
    }

    public void model(final String name, final JsonObject model) {
        write(root.resolve("assets/minecraft/models").resolve(name + ".json"), model);
    }

    public void blockState(final String name, final JsonObject blockState) {
        write(root.resolve("assets/minecraft/blockstates").resolve(name + ".json"), blockState);
    }

    public static JsonObject geometry(final List<CuboidExtractor.Element> elements) {
        JsonArray array = new JsonArray();
        for (CuboidExtractor.Element element : elements) {
            JsonObject cuboid = new JsonObject();
            cuboid.add("from", vector(element.from().x, element.from().y, element.from().z));
            cuboid.add("to", vector(element.to().x, element.to().y, element.to().z));

            JsonObject faces = new JsonObject();
            for (Map.Entry<Direction, CuboidExtractor.Face> entry : element.faces().entrySet()) {
                CuboidExtractor.Face face = entry.getValue();
                JsonObject json = new JsonObject();
                json.addProperty("texture", "#texture");
                json.add("uv", vector(face.minU(), face.minV(), face.maxU(), face.maxV()));
                if (face.rotation() != 0) {
                    json.addProperty("rotation", face.rotation());
                }
                faces.add(entry.getKey().getName(), json);
            }
            cuboid.add("faces", faces);
            array.add(cuboid);
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/block");
        model.add("elements", array);
        return model;
    }

    public static JsonObject textured(final String parent, final String particle, final String texture) {
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", particle);
        textures.addProperty("texture", texture);

        JsonObject model = new JsonObject();
        model.addProperty("parent", parent);
        model.add("textures", textures);
        return model;
    }

    public static JsonObject variants(final Map<String, String> variants) {
        JsonObject entries = new JsonObject();
        variants.forEach((state, model) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("model", model);
            entries.add(state, entry);
        });

        JsonObject blockState = new JsonObject();
        blockState.add("variants", entries);
        return blockState;
    }

    private static JsonArray vector(final float... values) {
        JsonArray array = new JsonArray();
        for (float value : values) {
            array.add(value);
        }
        return array;
    }

    private static void write(final Path path, final JsonObject json) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json) + System.lineSeparator());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
