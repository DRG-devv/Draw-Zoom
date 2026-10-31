package com.example.bowzoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class BowZoomClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BowZoomConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(BowZoom::tick);
    }
}
