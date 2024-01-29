package me.cortex.nvidium.mixin.sodium;

import me.cortex.nvidium.sodiumCompat.IRepackagedResult;
import me.cortex.nvidium.sodiumCompat.RepackagedSectionOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkBuildOutput.class, remap = false)
public class MixinChunkBuildOutput implements IRepackagedResult {

    @Unique
    private RepackagedSectionOutput repackagedSectionOutput;

    @Inject(method = "<init>", at = @At("RETURN")) // Inject at construction
    private void initializeRepackagedOutput(CallbackInfo ci) {
        this.repackagedSectionOutput = new RepackagedSectionOutput(); // Initialize
    }

    @Override
    public RepackagedSectionOutput getOutput() {
        return repackagedSectionOutput;
    }

    @Override
    public void set(RepackagedSectionOutput output) {
        this.repackagedSectionOutput = output;
    }

    @Inject(method = "delete", at = @At("HEAD"))
    private void cleanup(CallbackInfo ci) {
        if (repackagedSectionOutput != null) {
            repackagedSectionOutput.delete();
        }
    }
}
