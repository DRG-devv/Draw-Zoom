    private static class InfoIcon extends PressableWidget {
        private static final int SIZE = 10;
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
                    Text.literal("i"), cx, getY() + 1, 0xFF000000);
        }

        @Override
        public void onPress() {
            action.run();
        }
    }
