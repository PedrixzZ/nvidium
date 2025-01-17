package me.cortex.nvidium.mixin.minecraft;

import me.cortex.nvidium.Nvidium;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    private static final float MAX_FAR_PLANE_DISTANCE = 16 * 512f; // Precalculate

    @Inject(method = "getFarPlaneDistance", at = @At("HEAD"), cancellable = true)
    public void method_32796(CallbackInfoReturnable<Float> cir) {
        if (Nvidium.IS_ENABLED) {
            cir.setReturnValue(MAX_FAR_PLANE_DISTANCE); // Use the constant
            cir.cancel();
        }
    }
}
