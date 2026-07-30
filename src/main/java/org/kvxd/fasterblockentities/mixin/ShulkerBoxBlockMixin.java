package org.kvxd.fasterblockentities.mixin;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.kvxd.fasterblockentities.FBEConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin {

    protected RenderShape getRenderShape(final BlockState state) {
        return FBEConfig.STATIC_SHULKER_BOXES ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }
}
