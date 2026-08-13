package sectersion.tnttrails.client;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class TNTConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("tnttrails.properties");
    private static final int[] COLORS = {0xFFFF0000, 0xFFFF6600, 0xFFFFFF00, 0xFF00FFFF, 0xFF00FF66, 0xFFCC33FF};
    private static final String[] COLOR_NAMES = {"Red", "Orange", "Yellow", "Cyan", "Green", "Purple"};
    private static int trailLifetimeSeconds = 15;
    private static int startColor = 0;
    private static int endColor = 1;
    private static int lineWidth = 3;

    private TNTConfig() {}

    public static void load() {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            properties.load(reader);
            trailLifetimeSeconds = clamp(Integer.parseInt(properties.getProperty("trailLifetimeSeconds", "15")), 1, 60);
            startColor = clamp(Integer.parseInt(properties.getProperty("startColor", "0")), 0, COLORS.length - 1);
            endColor = clamp(Integer.parseInt(properties.getProperty("endColor", "1")), 0, COLORS.length - 1);
            lineWidth = clamp(Integer.parseInt(properties.getProperty("lineWidth", "3")), 1, 8);
        } catch (IOException | NumberFormatException ignored) {
            save();
        }
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("trailLifetimeSeconds", Integer.toString(trailLifetimeSeconds));
        properties.setProperty("startColor", Integer.toString(startColor));
        properties.setProperty("endColor", Integer.toString(endColor));
        properties.setProperty("lineWidth", Integer.toString(lineWidth));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { properties.store(writer, "TNT Trails client settings"); }
        } catch (IOException ignored) {
        }
    }

    private static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }
    public static int lifetimeMillis() { return trailLifetimeSeconds * 1000; }
    public static int lifetimeSeconds() { return trailLifetimeSeconds; }
    public static void cycleLifetime() { trailLifetimeSeconds = trailLifetimeSeconds >= 60 ? 1 : trailLifetimeSeconds + 1; save(); }
    public static int startColor() { return COLORS[startColor]; }
    public static int endColor() { return COLORS[endColor]; }
    public static String startColorName() { return COLOR_NAMES[startColor]; }
    public static String endColorName() { return COLOR_NAMES[endColor]; }
    public static void cycleStartColor() { startColor = (startColor + 1) % COLORS.length; save(); }
    public static void cycleEndColor() { endColor = (endColor + 1) % COLORS.length; save(); }
    public static int lineWidth() { return lineWidth; }
    public static void cycleLineWidth() { lineWidth = lineWidth >= 8 ? 1 : lineWidth + 1; save(); }
}
