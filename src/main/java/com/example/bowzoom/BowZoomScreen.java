package com.example.bowzoom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class BowZoomScreen extends Screen {
    private static final int BUBBLE_WIDTH = 200;
    private static final int TEXT_WIDTH = 188;

    private final Screen parent;
    private int infoIndex = -1;

    private static final String[] INFO = {
            "Zoom strength: how far the view narrows at full draw. 1.0 = no zoom, 0.7 = FOV x 0.7, lower = stronger.",
            "Zoom speed: how fast the zoom moves in and out. Higher = faster, 1.0 = instant.",
            "Trident charge ticks: how many ticks of holding it takes to reach full zoom. Lower = faster."
    };

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

        addDrawableChild(new InfoIcon(x + 204, y + 2, () -> toggleInfo(0)));
        addDrawableChild(new InfoIcon(x + 204, y + 32, () -> toggleInfo(1)));
        addDrawableChild(new InfoIcon(x + 204, y + 62, () -> toggleInfo(2)));

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x, y + 100, 200, 20).build());
    }

    private void toggleInfo(int index) {
        infoIndex = (infoIndex == index) ? -1 : index;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (infoIndex >= 0) {
            List<OrderedText> lines = this.textRenderer.wrapLines(
                   Text.literal(INFO[infoIndex]), TEXT_WIDTH);

            int bubbleX = this.width / 2 - BUBBLE_WIDTH / 2;
            int bubbleY = this.height / 2 + 90;
            int bubbleHeight = lines.size() * 10 + 10;

            context.fill(bubbleX, bubbleY, bubbleX + BUBBLE_WIDTH, bubbleY + bubbleHeight, 0xDD000000);
            context.fill(bubbleX, bubbleY, bubbleX + BUBBLE_WIDTH, bubbleY + 1, 0xFFFFFFFF);

            int lineY = bubbleY + 5;
            for (OrderedText line : lines) {
                context.drawTextWithShadow(this.textRenderer, line, bubbleX + 6, lineY, 0xFFFFFF);
                lineY += 10;
            }
        }
    }

    @Override
    public void close() {
        BowZoomConfig.save();
        MinecraftClient.getInstance().setScreen(parent);
    }

    private static class InfoIcon extends PressableWidget {
        private static final int SIZE = 16;
        private final Runnable action;

        InfoIcon(int x, int y, Runnable action) {
            super(x, y, SIZE, SIZE, Text.literal("i"));
            this.action = action;
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
            int cx = getX() + SIZE / 2;
            int cy = getY() + SIZE / 2;
            int r = SIZE / 2;
            int color = isHovered() ? 0xFFFFFFFF : 0xFFAAAAAA;

            for (int dy = -r; dy <= r; dy++) {
                int half = (int) Math.round(Math.sqrt(r * r - dy * dy));
                context.fill(cx - half, cy + dy, cx + half + 1, cy + dy + 1, color);
            }

            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,
                    Text.literal("i"), cx, getY() + 4, 0xFF000000);
        }

        @Override
        public void onPress(AbstractInput input) {
            action.run();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
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
