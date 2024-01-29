package me.cortex.nvidium.mixin.minecraft;

import me.cortex.nvidium.Nvidium;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    private static final int MAX_RENDER_DISTANCE = 9999999;
    private static final int KEEP_DISTANCE_32 = 16 * 32;
    private static final int KEEP_DISTANCE_256 = 16 * 256;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
    private float redirectMax(float a, float b) {
        return Nvidium.IS_ENABLED ? a : Math.max(a, b);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getViewDistance()F"))
    private float changeRD(GameRenderer instance) {
        float viewDistance = instance.getViewDistance();
        return Nvidium.IS_ENABLED ?
                (Nvidium.config.region_keep_distance * 16 == KEEP_DISTANCE_32 ? viewDistance : MAX_RENDER_DISTANCE) :
                viewDistance;
    }
}
