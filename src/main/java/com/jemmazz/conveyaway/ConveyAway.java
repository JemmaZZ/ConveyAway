package com.jemmazz.conveyaway;

import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import com.jemmazz.conveyaway.init.ConveyAwayBlocks;
import com.jemmazz.conveyaway.init.ConveyAwayItems;
import com.jemmazz.conveyaway.init.ConveyAwaySounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConveyAway implements ModInitializer
{
    public static final String MODID = "conveyaway";
    public static final Logger LOGGER = LogManager.getLogger( MODID );

    public static ItemGroup generalItemGroup = FabricItemGroupBuilder.build( new Identifier( MODID, "general" ), () -> new ItemStack( ConveyAwayBlocks.CONVEYOR ) );

    public static Identifier id( String name )
    {
        return new Identifier( MODID, name );
    }

    @Override
    public void onInitialize()
    {
        ConveyAwayBlocks.init();
        ConveyAwayBlockEntities.init();
        ConveyAwayItems.init();
        ConveyAwaySounds.init();
    }
}
