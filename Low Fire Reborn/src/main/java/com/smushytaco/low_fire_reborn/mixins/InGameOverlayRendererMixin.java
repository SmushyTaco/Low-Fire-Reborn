package com.smushytaco.low_fire_reborn.mixins;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smushytaco.low_fire_reborn.LowFire;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private static void hookRenderFireOverlayOne(PoseStack matrices, MultiBufferSource vertexConsumers, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (!LowFire.INSTANCE.getConfig().getEnableLowFire()) return;
        matrices.translate(0.0, LowFire.INSTANCE.getConfig().getFireOffset(), 0.0);
    }
    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void hookRenderFireOverlayTwo(PoseStack matrices, MultiBufferSource vertexConsumers, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (LowFire.INSTANCE.getConfig().getShouldRenderFire()) return;
        ci.cancel();
    }
}