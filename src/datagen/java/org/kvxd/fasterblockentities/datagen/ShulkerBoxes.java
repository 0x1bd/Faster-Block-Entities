package org.kvxd.fasterblockentities.datagen;

import net.minecraft.client.model.monster.shulker.ShulkerModel;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ShulkerBoxes {

    private static final String TEMPLATE = "block/template_shulker_box_";

    public static void generate(final Assets assets) {
        for (Direction facing : Direction.values()) {
            List<CuboidExtractor.Element> elements = CuboidExtractor.extract(
                ShulkerModel.createBoxLayer(),
                ShulkerBoxRenderer.modelTransform(facing).getMatrix()
            );
            assets.model(TEMPLATE + facing.getName(), Assets.geometry(elements));
        }

        box(assets, "shulker_box", "shulker");
        for (DyeColor color : DyeColor.values()) {
            box(assets, color.getName() + "_shulker_box", "shulker_" + color.getName());
        }
    }

    private static void box(final Assets assets, final String block, final String sprite) {
        Map<String, String> variants = new LinkedHashMap<>();
        for (Direction facing : Direction.values()) {
            String model = "block/" + block + "_" + facing.getName();
            assets.model(model, Assets.textured(
                "minecraft:" + TEMPLATE + facing.getName(),
                "minecraft:block/" + block,
                "minecraft:entity/shulker/" + sprite
            ));
            variants.put("facing=" + facing.getName(), "minecraft:" + model);
        }
        assets.blockState(block, Assets.variants(variants));
    }

    private ShulkerBoxes() {
    }
}
