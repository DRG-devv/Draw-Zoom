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
    private static final int BOX_WIDTH = 150;
    private static final int TEXT_WIDTH = 138;
    private static final int ARROW_LENGTH = 6;
    private static final int GAP = 8;

    private final Screen parent;

    private int infoIndex = -1;
    private int anchorX;
    private int anchorY;

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

        int iconX = x + 204;
        addDrawableChild(new InfoIcon(iconX, y + 2, () -> toggleInfo(0, iconX + 8, y + 10)));
        addDrawableChild(new InfoIcon(iconX, y + 32, () -> toggleInfo(1, iconX + 8, y + 40)));
        addDrawableChild(new InfoIcon(iconX, y + 62, () -> toggleInfo(2, iconX + 8, y + 70)));

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x, y + 100, 200, 20).build());
    }

    private void toggleInfo(int index, int cx, int cy) {
        if (infoIndex == index) {
            infoIndex = -1;
        } else {
            infoIndex = index;
            anchorX = cx;
            anchorY = cy;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (infoIndex < 0) {
            return;
        }

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 400);

        List<OrderedText> lines = this.textRenderer.wrapLines(
                Text.literal(INFO[infoIndex]), TEXT_WIDTH);
        int boxHeight = lines.size() * 10 + 8;

        // Put the box to the right of the icon if it fits, otherwise to the left
        boolean rightSide = anchorX + GAP + ARROW_LENGTH + BOX_WIDTH < this.width;
        int boxX = rightSide
                ? anchorX + GAP + ARROW_LENGTH
                : anchorX - GAP - ARROW_LENGTH - BOX_WIDTH;

        // Center the box on the icon, keeping it on screen
        int boxY = anchorY - boxHeight / 2;
        boxY = Math.max(4, Math.min(boxY, this.height - boxHeight - 4));

        int boxColor = 0xEE000000;
        int borderColor = 0xFFFFFFFF;
        context.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + boxHeight, boxColor);
        context.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + 1, borderColor);
        context.fill(boxX, boxY + boxHeight - 1, boxX + BOX_WIDTH, boxY + boxHeight, borderColor);
        context.fill(boxX, boxY, boxX + 1, boxY + boxHeight, borderColor);
        context.fill(boxX + BOX_WIDTH - 1, boxY, boxX + BOX_WIDTH, boxY + boxHeight, borderColor);

        // Arrow from the box edge to the icon
        if (rightSide) {
            int tipX = anchorX + GAP / 2;
            for (int k = 0; k < ARROW_LENGTH; k++) {
                context.fill(tipX + k, anchorY - k, tipX + k + 1, anchorY + k + 1, borderColor);
            }
        } else {
            int tipX = anchorX - GAP / 2;
            for (int k = 0; k < ARROW_LENGTH; k++) {
                context.fill(tipX - k - 1, anchorY - k, tipX - k, anchorY + k + 1, borderColor);
            }
        }

        int lineY = boxY + 4;
        for (OrderedText line : lines) {
            context.drawTextWithShadow(this.textRenderer, line, boxX + 6, lineY, 0xFFFFFF);
            lineY += 10;
        }

        context.getMatrices().pop();
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
