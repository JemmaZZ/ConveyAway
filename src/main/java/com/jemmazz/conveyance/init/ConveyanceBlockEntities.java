package com.jemmazz.conveyance.init;

import com.jemmazz.conveyance.Conveyance;
import com.jemmazz.conveyance.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyance.blocks.entities.FunnelBlockEntity;
import com.jemmazz.conveyance.blocks.entities.InserterBlockEntity;
import com.jemmazz.conveyance.blocks.entities.VerticalConveyorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ConveyanceBlockEntities
{
    public static BlockEntityType CONVEYOR = register( "conveyor", create( ConveyorBlockEntity::new, ConveyanceBlocks.CONVEYOR, ConveyanceBlocks.FAST_CONVEYOR, ConveyanceBlocks.EXPRESS_CONVEYOR, ConveyanceBlocks.EXTREME_CONVEYOR ) );
    public static BlockEntityType VERTICAL_CONVEYOR = register( "vertical_conveyor", create( VerticalConveyorBlockEntity::new, ConveyanceBlocks.VERTICAL_CONVEYOR, ConveyanceBlocks.VERTICAL_FAST_CONVEYOR, ConveyanceBlocks.VERTICAL_EXPRESS_CONVEYOR, ConveyanceBlocks.VERTICAL_EXTREME_CONVEYOR ) );

    public static BlockEntityType FUNNEL = register( "funnel", create( FunnelBlockEntity::new, ConveyanceBlocks.FUNNEL ) );

    public static BlockEntityType INSERTER = register( "inserter", create( InserterBlockEntity::new, ConveyanceBlocks.INSERTER, ConveyanceBlocks.FAST_INSERTER ) );

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
        return Registry.register( Registry.BLOCK_ENTITY_TYPE, Conveyance.id( name ), builder.build( null ) );
    }
}
