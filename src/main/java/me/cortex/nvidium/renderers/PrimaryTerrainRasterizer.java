package me.cortex.nvidium.renderers;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.mojang.blaze3d.platform.GlStateManager;
import me.cortex.nvidium.gl.shader.Shader;
import me.cortex.nvidium.sodiumCompat.ShaderLoader;
import me.cortex.nvidium.mixin.minecraft.LightMapAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL45C;

import static me.cortex.nvidium.RenderPipeline.GL_DRAW_INDIRECT_ADDRESS_NV;
import static me.cortex.nvidium.gl.shader.ShaderType.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL33.glGenSamplers;
import static org.lwjgl.opengl.NVMeshShader.glMultiDrawMeshTasksIndirectNV;
import static org.lwjgl.opengl.NVVertexBufferUnifiedMemory.glBufferAddressRangeNV;

public class PrimaryTerrainRasterizer extends Phase {
    private final int blockSampler = glGenSamplers();
    private final int lightSampler = glGenSamplers();
    private final Shader shader = Shader.make()
            .addSource(TASK, ShaderLoader.parse(new Identifier("nvidium", "terrain/task.glsl")))
            .addSource(MESH, ShaderLoader.parse(new Identifier("nvidium", "terrain/mesh.glsl")))
            .addSource(FRAGMENT, ShaderLoader.parse(new Identifier("nvidium", "terrain/frag.frag"))).compile();

    public PrimaryTerrainRasterizer() {
        GLES30.glSamplerParameteri(blockSampler, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST_MIPMAP_LINEAR);
        GLES30.glSamplerParameteri(blockSampler, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glSamplerParameteri(blockSampler, GLES30.GL_TEXTURE_MIN_LOD, 0);
        GLES30.glSamplerParameteri(blockSampler, GLES30.GL_TEXTURE_MAX_LOD, 4);
    }

    private static void setTexture(int textureId, int bindingPoint) {
        GLES30.glActiveTexture(33984 + bindingPoint);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
    }

    public void raster(int regionCount, long commandAddr) {
        shader.bind();

        int blockId = loadTexture(new Identifier("minecraft", "textures/atlas/blocks.png"));
        int lightId = ((LightMapAccessor)MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager()).getTexture().getGlId();

        GLES30.glBindSampler(0, blockSampler);
        GLES30.glBindSampler(1, lightSampler);
        setTexture(blockId, 0);
        setTexture(lightId, 1);

        glBufferAddressRangeNV(GL_DRAW_INDIRECT_ADDRESS_NV, 0, commandAddr, regionCount*8L);//Bind the command buffer
        glMultiDrawMeshTasksIndirectNV( 0, regionCount, 0);
        GLES30.glBindSampler(0, 0);
        GLES30.glBindSampler(1, 0);
    }

    private int loadTexture(Identifier textureId) {
        // TODO: Implement texture loading for Android
        return 0;
    }

    public void delete() {
        GLES30.glDeleteSamplers(blockSampler);
        GLES30.glDeleteSamplers(lightSampler);
        shader.delete();
    }
}
