package com.example.bowzoom;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BowZoomConfig {
    private static final String HEADER = "Bow Zoom settings. zoom_strength: 0.1-1.0 (lower = stronger zoom). "
            + "zoom_speed: 0.01-1.0 (higher = faster). trident_charge_ticks: 1-40.";

    public static float zoomStrength = 0.70f;
    public static float zoomSpeed = 0.6f;
    public static float tridentChargeTicks = 5.0f;

    private BowZoomConfig() {}

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("bowzoom.properties");
    }

    public static void load() {
        Path path = path();
        Properties props = new Properties();
        props.setProperty("zoom_strength", "0.70");
        props.setProperty("zoom_speed", "0.6");
        props.setProperty("trident_charge_ticks", "5.0");

        try {
            if (Files.exists(path)) {
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                }
            } else {
                save();
                return;
            }
        } catch (Exception e) {
            // Use defaults if the file can't be read
        }

        zoomStrength = clamp(parse(props, "zoom_strength", 0.70f), 0.1f, 1.0f);
        zoomSpeed = clamp(parse(props, "zoom_speed", 0.6f), 0.01f, 1.0f);
        tridentChargeTicks = clamp(parse(props, "trident_charge_ticks", 5.0f), 1.0f, 40.0f);
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("zoom_strength", String.valueOf(zoomStrength));
        props.setProperty("zoom_speed", String.valueOf(zoomSpeed));
        props.setProperty("trident_charge_ticks", String.valueOf(tridentChargeTicks));

        try {
            Path path = path();
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, HEADER);
            }
        } catch (Exception e) {
            // Settings still apply for this session if the file can't be written
        }
    }

    private static float parse(Properties props, String key, float fallback) {
        try {
            return Float.parseFloat(props.getProperty(key));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
