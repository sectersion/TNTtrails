package sectersion.tnttrails.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TNTTracker {
    private static final TNTTracker INSTANCE = new TNTTracker();
    private static final long MAX_AGE_MS = 15000L;
    private final Map<Integer, List<PositionSnapshot>> positionsByEntityId = new ConcurrentHashMap<>();
    private final List<ExplosionMarker> explosions = new CopyOnWriteArrayList<>();

    private TNTTracker() {}

    public static TNTTracker getInstance() { return INSTANCE; }

    public void registerTNT(int entityId, double x, double y, double z) {
        positionsByEntityId.computeIfAbsent(entityId, ignored -> new ArrayList<>())
            .add(new PositionSnapshot(x, y, z, System.currentTimeMillis()));
        pruneOldEntries();
    }

    public void updatePosition(int entityId, double x, double y, double z) {
        registerTNT(entityId, x, y, z);
    }

    public void removeTNT(int entityId) { positionsByEntityId.remove(entityId); }

    public void addExplosion(double x, double y, double z) {
        explosions.add(new ExplosionMarker(x, y, z, System.currentTimeMillis()));
    }

    public List<ExplosionMarker> getExplosions() { return List.copyOf(explosions); }

    public Map<Integer, List<PositionSnapshot>> getAllPositions() {
        Map<Integer, List<PositionSnapshot>> copy = new ConcurrentHashMap<>();
        positionsByEntityId.forEach((id, positions) -> {
            synchronized (positions) { copy.put(id, List.copyOf(positions)); }
        });
        return Map.copyOf(copy);
    }

    public void pruneOldEntries() {
        long now = System.currentTimeMillis();
        positionsByEntityId.entrySet().removeIf(entry -> {
            List<PositionSnapshot> positions = entry.getValue();
            synchronized (positions) {
                positions.removeIf(snapshot -> now - snapshot.timestamp() > MAX_AGE_MS);
                return positions.isEmpty();
            }
        });
        explosions.removeIf(marker -> now - marker.timestamp() > MAX_AGE_MS);
    }

    public void clear() { positionsByEntityId.clear(); }

    public record PositionSnapshot(double x, double y, double z, long timestamp) {}
    public record ExplosionMarker(double x, double y, double z, long timestamp) {}
}
