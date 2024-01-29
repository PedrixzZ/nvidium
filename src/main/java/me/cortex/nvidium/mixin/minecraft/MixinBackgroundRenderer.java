package me.cortex.nvidium.mixin.minecraft;

import me.cortex.nvidium.Nvidium;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    private static final float MAX_FOG_DISTANCE = 9999999f; // Precalculate

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 192.0F))
    private static float changeFog(float fog) {
        return Nvidium.IS_ENABLED ? MAX_FOG_DISTANCE : fog; // Concise conditional
    }
}
