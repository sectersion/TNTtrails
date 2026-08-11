package sectersion.tnttrails.client.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sectersion.tnttrails.client.TNTRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void tnttrails$render(GraphicsResourceAllocator allocator, DeltaTracker delta,
                                  boolean renderBlockOutline, CameraRenderState camera,
                                  Matrix4fc viewProjection, GpuBufferSlice fog,
                                  Vector4f clearColor, boolean renderSky, CallbackInfo info) {
        TNTRenderer.render();
    }
}
