package org.kvxd.fasterblockentities.mixin;

import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.kvxd.fasterblockentities.FBEConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractChestBlock.class)
public abstract class AbstractChestBlockMixin {

    protected RenderShape getRenderShape(final BlockState state) {
        return FBEConfig.STATIC_CHESTS ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }
}
