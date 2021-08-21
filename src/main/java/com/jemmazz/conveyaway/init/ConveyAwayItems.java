package com.jemmazz.conveyaway.init;

import com.jemmazz.conveyaway.ConveyAway;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ConveyAwayItems {
    public static void init() {
        // NO-OP
    }

    private static <T extends Item> T register(String name, T item) {
        Registry.register(Registry.ITEM, ConveyAway.id(name), item);

        return item;
    }
}
