package com.jemmazz.conveyance.init;

import com.jemmazz.conveyance.Conveyance;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ConveyanceItems
{
    public static void init()
    {
        // NO-OP
    }

    private static <T extends Item> T register( String name, T item )
    {
        Registry.register( Registry.ITEM, Conveyance.id( name ), item );

        return item;
    }
}
