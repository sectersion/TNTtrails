package sectersion.tnttrails.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class TNTRenderer {
    private static final float LINE_WIDTH = 2.5f;
    private static final float GLOW_WIDTH = 5.0f;

    public static void render(WorldRenderContext context) {
        PoseStack matrices = context.matrices();
        MultiBufferSource consumers = context.consumers();
        if (matrices == null || consumers == null) return;
        matrices.pushPose();
        VertexConsumer lines = consumers.getBuffer(RenderType.lines());
        long now = System.currentTimeMillis();
        for (var positions : TNTTracker.getInstance().getAllPositions().values()) {
            for (int i = 0; i + 1 < positions.size(); i++) {
                var from = smooth(positions, i);
                var to = smooth(positions, i + 1);
                long age = Math.max(0L, now - positions.get(i + 1).timestamp());
                float freshness = Math.max(0.0f, 1.0f - (float) age / TNTConfig.lifetimeMillis());
                float pathProgress = (float) i / (positions.size() - 1);
                int color = colorFor(pathProgress, 0.50f * freshness);

                line(lines, matrices, from, to, colorFor(pathProgress, 0.12f * freshness));
                line(lines, matrices, from, to, color);
            }
        }
        for (var marker : TNTTracker.getInstance().getExplosions()) {
            Vec3 center = new Vec3(marker.x(), marker.y(), marker.z());
            box(lines, matrices, new AABB(center.x - 0.35, center.y - 0.35, center.z - 0.35,
                    center.x + 0.35, center.y + 0.35, center.z + 0.35), 0xFF33AAFF);
        }
        matrices.popPose();
    }

    private static void line(VertexConsumer consumer, PoseStack matrices, Vec3 from, Vec3 to, int color) {
        PoseStack.Pose pose = matrices.last();
        consumer.addVertex(pose, (float) from.x, (float) from.y, (float) from.z)
                .setColor(color).setNormal(pose, (float) (to.x - from.x), (float) (to.y - from.y), (float) (to.z - from.z));
        consumer.addVertex(pose, (float) to.x, (float) to.y, (float) to.z)
                .setColor(color).setNormal(pose, (float) (to.x - from.x), (float) (to.y - from.y), (float) (to.z - from.z));
    }

    private static void box(VertexConsumer consumer, PoseStack matrices, AABB box, int color) {
        Vec3[] p = {new Vec3(box.minX, box.minY, box.minZ), new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ), new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ), new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.maxZ), new Vec3(box.minX, box.maxY, box.maxZ)};
        int[][] edges = {{0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},{0,4},{1,5},{2,6},{3,7}};
        for (int[] edge : edges) line(consumer, matrices, p[edge[0]], p[edge[1]], color);
    }

    private static Vec3 smooth(java.util.List<TNTTracker.PositionSnapshot> positions, int index) {
        var point = positions.get(index);
        if (index == 0 || index == positions.size() - 1) {
            return new Vec3(point.x(), point.y(), point.z());
        }
        var previous = positions.get(index - 1);
        var next = positions.get(index + 1);
        return new Vec3((previous.x() + point.x() + next.x()) / 3.0,
                (previous.y() + point.y() + next.y()) / 3.0,
                (previous.z() + point.z() + next.z()) / 3.0);
    }

    private static int colorFor(float progress, float alpha) {
        // The path starts red and transitions to orange at the TNT.
        int start = TNTConfig.startColor();
        int end = TNTConfig.endColor();
        int red = (int) (((start >> 16 & 255) * (1.0f - progress)) + ((end >> 16 & 255) * progress));
        int green = (int) (((start >> 8 & 255) * (1.0f - progress)) + ((end >> 8 & 255) * progress));
        int blue = (int) (((start & 255) * (1.0f - progress)) + ((end & 255) * progress));
        int opacity = Math.max(0, Math.min(255, (int) (alpha * 255.0f)));
        return opacity << 24 | red << 16 | green << 8 | blue;
    }
}
