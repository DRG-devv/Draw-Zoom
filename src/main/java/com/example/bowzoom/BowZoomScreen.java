package com.example.bowzoom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class BowZoomScreen extends Screen {
    private final Screen parent;

    public BowZoomScreen(Screen parent) {
        super(Text.literal("Bow Zoom Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 50;

        addDrawableChild(new Slider(x, y, 200, 20, "Zoom strength", 0.1, 1.0,
                BowZoomConfig.zoomStrength, v -> BowZoomConfig.zoomStrength = v.floatValue()));
        addDrawableChild(new Slider(x, y + 30, 200, 20, "Zoom speed", 0.01, 1.0,
                BowZoomConfig.zoomSpeed, v -> BowZoomConfig.zoomSpeed = v.floatValue()));
        addDrawableChild(new Slider(x, y + 60, 200, 20, "Trident charge ticks", 1.0, 40.0,
                BowZoomConfig.tridentChargeTicks, v -> BowZoomConfig.tridentChargeTicks = v.floatValue()));

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x, y + 100, 200, 20)
                .build());
    }

    @Override
    public void close() {
        BowZoomConfig.save();
        MinecraftClient.getInstance().setScreen(parent);
    }

    private static class Slider extends SliderWidget {
        private final String label;
        private final double min;
        private final double max;
        private final Consumer<Double> setter;

        Slider(int x, int y, int width, int height, String label,
               double min, double max, double current, Consumer<Double> setter) {
            super(x, y, width, height, Text.empty(), (current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + String.format("%.2f", min + (max - min) * this.value)));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + (max - min) * this.value);
        }
    }
}
