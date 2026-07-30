package org.kvxd.fasterblockentities.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.kvxd.fasterblockentities.FBEConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(method = "getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity, S extends BlockEntityRenderState> void fasterblockentities$skipStaticRenderer(
        final E blockEntity,
        final CallbackInfoReturnable<BlockEntityRenderer<E, S>> cir
    ) {
        if (fasterblockentities$isStatic(blockEntity)) {
            cir.setReturnValue(null);
        }
    }

    private static boolean fasterblockentities$isStatic(final BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity) {
            return FBEConfig.STATIC_CHESTS;
        }
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            return FBEConfig.STATIC_SHULKER_BOXES;
        }
        return false;
    }
}
