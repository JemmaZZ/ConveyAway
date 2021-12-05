package com.jemmazz.conveyance.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ConveyorSyncHandler
{
    public static float position = 0;
    public static float prevPosition = 0;

    public static void init()
    {
        ClientTickEvents.START_WORLD_TICK.register( world -> {
            if( position < 15 )
            {
                prevPosition = position;
                position += 1;
            }
            else
            {
                prevPosition = -1;
                position = 0;
            }
        } );
    }
}
