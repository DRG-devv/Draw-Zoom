package com.example.bowzoom.mixin;

import com.example.bowzoom.BowZoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void bowzoom$applyZoom(Camera camera, float tickDelta, boolean changingFov,
                                   CallbackInfoReturnable<Float> cir) {
        float zoom = BowZoom.getMultiplier(tickDelta);
        if (zoom != 1.0f) {
            cir.setReturnValue(cir.getReturnValue() * zoom);
        }
    }
}
