package com.jemmazz.conveyaway.client;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;

import java.util.ArrayList;
import java.util.HashMap;

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
