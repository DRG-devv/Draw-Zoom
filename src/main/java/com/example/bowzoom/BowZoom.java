package com.example.bowzoom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.MathHelper;

public final class BowZoom {
    private static final float EPSILON = 0.002f;

    private static float previous = 1.0f;
    private static float current = 1.0f;

    private BowZoom() {}

    public static void tick(MinecraftClient client) {
        previous = current;

        if (shouldResetInstantly(client)) {
            previous = 1.0f;
            current = 1.0f;
            return;
        }

        float target = getTargetMultiplier(client);
        current += (target - current) * BowZoomConfig.zoomSpeed;
        if (Math.abs(target - current) < EPSILON) {
            current = target;
        }
    }

    public static float getMultiplier(float tickDelta) {
        return MathHelper.lerp(tickDelta, previous, current);
    }

    private static boolean shouldResetInstantly(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        return client.world == null
                || player == null
                || !player.isAlive()
                || client.currentScreen != null;
    }

    private static float getTargetMultiplier(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (!player.isUsingItem()) {
            return 1.0f;
        }

        ItemStack stack = player.getActiveItem();
        float charge;
        if (stack.getItem() instanceof BowItem) {
            charge = BowItem.getPullProgress(player.getItemUseTime());
        } else if (stack.getItem() instanceof TridentItem) {
            charge = MathHelper.clamp(player.getItemUseTime() / BowZoomConfig.tridentChargeTicks, 0.0f, 1.0f);
        } else {
            return 1.0f;
        }

        return 1.0f - (1.0f - BowZoomConfig.zoomStrength) * charge;
    }
}
