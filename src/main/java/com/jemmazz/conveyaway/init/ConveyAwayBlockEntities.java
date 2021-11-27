package com.jemmazz.conveyaway.init;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyaway.blocks.entities.FunnelBlockEntity;
import com.jemmazz.conveyaway.blocks.entities.VerticalConveyorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ConveyAwayBlockEntities
{
    public static BlockEntityType CONVEYOR = register( "conveyor", create( ConveyorBlockEntity::new, ConveyAwayBlocks.CONVEYOR, ConveyAwayBlocks.FAST_CONVEYOR, ConveyAwayBlocks.EXPRESS_CONVEYOR, ConveyAwayBlocks.EXTREME_CONVEYOR ) );
    public static BlockEntityType VERTICAL_CONVEYOR = register( "vertical_conveyor", create( VerticalConveyorBlockEntity::new, ConveyAwayBlocks.VERTICAL_CONVEYOR, ConveyAwayBlocks.VERTICAL_FAST_CONVEYOR, ConveyAwayBlocks.VERTICAL_EXPRESS_CONVEYOR, ConveyAwayBlocks.VERTICAL_EXTREME_CONVEYOR ) );

    public static BlockEntityType FUNNEL = register( "funnel", create( FunnelBlockEntity::new, ConveyAwayBlocks.FUNNEL ) );



    public static void init()
    {
        // NO-OP
    }

    private static <T extends BlockEntity> FabricBlockEntityTypeBuilder<T> create( FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks )
    {
        return FabricBlockEntityTypeBuilder.create( factory, blocks );
    }

    private static <T extends BlockEntity> BlockEntityType<T> register( String name, FabricBlockEntityTypeBuilder<T> builder )
    {
        return Registry.register( Registry.BLOCK_ENTITY_TYPE, ConveyAway.id( name ), builder.build( null ) );
    }
}
