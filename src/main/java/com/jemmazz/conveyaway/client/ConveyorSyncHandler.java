package com.jemmazz.conveyaway.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ConveyorSyncHandler {
    public static float position = 0;
    public static float prevPosition = 0;

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            prevPosition = position;
            position += 1;
        });
    }
}
