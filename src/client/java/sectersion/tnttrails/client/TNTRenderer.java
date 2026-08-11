package sectersion.tnttrails.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.gizmos.GizmoStyle;

@Environment(EnvType.CLIENT)
public class TNTRenderer {
    private static final long TRAIL_LIFETIME_MS = 15000L;
    private static final float LINE_WIDTH = 2.5f;
    private static final float GLOW_WIDTH = 5.0f;

    public static void render() {
        long now = System.currentTimeMillis();
        for (var positions : TNTTracker.getInstance().getAllPositions().values()) {
            for (int i = 0; i + 1 < positions.size(); i++) {
                var from = smooth(positions, i);
                var to = smooth(positions, i + 1);
                long age = Math.max(0L, now - positions.get(i + 1).timestamp());
                float freshness = Math.max(0.0f, 1.0f - (float) age / TRAIL_LIFETIME_MS);
                float pathProgress = (float) i / (positions.size() - 1);
                int color = colorFor(pathProgress, 0.50f * freshness);

                // A wider, faint pass gives the trail a soft glow without bypassing depth.
                Gizmos.line(from, to, colorFor(pathProgress, 0.12f * freshness), GLOW_WIDTH)
                    .persistForMillis(100);
                Gizmos.line(from, to, color, LINE_WIDTH)
                    .persistForMillis(100);
            }
        }
        for (var marker : TNTTracker.getInstance().getExplosions()) {
            Vec3 center = new Vec3(marker.x(), marker.y(), marker.z());
            Gizmos.cuboid(new AABB(center.x - 0.35, center.y - 0.35, center.z - 0.35,
                    center.x + 0.35, center.y + 0.35, center.z + 0.35),
                    GizmoStyle.fill(0x4033AAFF)).persistForMillis(100);
        }
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
        int red = 255;
        int green = (int) (progress * 190.0f);
        int blue = 0;
        int opacity = Math.max(0, Math.min(255, (int) (alpha * 255.0f)));
        return opacity << 24 | red << 16 | green << 8 | blue;
    }
}
