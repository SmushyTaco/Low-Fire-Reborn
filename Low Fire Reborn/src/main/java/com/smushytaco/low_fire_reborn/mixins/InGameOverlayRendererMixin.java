package com.smushytaco.low_fire_reborn.mixins;
import com.smushytaco.low_fire_reborn.LowFire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
	@Inject(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
	private static void hookRenderFireOverlayOne(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
		if (!LowFire.INSTANCE.getConfig().getEnableLowFire()) return;
		matrices.translate(0.0, LowFire.INSTANCE.getConfig().getFireOffset(), 0.0);
	}
	@Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
	private static void hookRenderFireOverlayTwo(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
		if (LowFire.INSTANCE.getConfig().getShouldRenderFire()) return;
		ci.cancel();
	}
}