package com.jemmazz.conveyance.init;

import com.jemmazz.conveyance.Conveyance;
import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.ConveyorBlock;
import com.jemmazz.conveyance.blocks.FunnelBlock;
import com.jemmazz.conveyance.blocks.InserterBlock;
import com.jemmazz.conveyance.blocks.VerticalConveyorBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class ConveyanceBlocks
{
    public static ConveyorBlock CONVEYOR = register( "conveyor", new ConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 16 ) );
    public static ConveyorBlock FAST_CONVEYOR = register( "fast_conveyor", new ConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 8 ) );
    public static ConveyorBlock EXPRESS_CONVEYOR = register( "express_conveyor", new ConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 4 ) );
    public static ConveyorBlock EXTREME_CONVEYOR = register( "extreme_conveyor", new ConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 2 ) );

    public static ConveyorBlock VERTICAL_CONVEYOR = register( "vertical_conveyor", new VerticalConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 16 ) );
    public static ConveyorBlock VERTICAL_FAST_CONVEYOR = register( "vertical_fast_conveyor", new VerticalConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 8 ) );
    public static ConveyorBlock VERTICAL_EXPRESS_CONVEYOR = register( "vertical_express_conveyor", new VerticalConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 4 ) );
    public static ConveyorBlock VERTICAL_EXTREME_CONVEYOR = register( "vertical_extreme_conveyor", new VerticalConveyorBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 2 ) );

    public static FunnelBlock FUNNEL = register( "funnel", new FunnelBlock( FabricBlockSettings.copyOf( Blocks.STONE ).sounds( BlockSoundGroup.METAL ).nonOpaque(), 3 ) );

    public static InserterBlock INSERTER = register( "inserter", new InserterBlock( "normal", 12, FabricBlockSettings.copy( Blocks.STONE ).nonOpaque() ) );
    public static InserterBlock FAST_INSERTER = register( "fast_inserter", new InserterBlock( "fast", 6, FabricBlockSettings.copy( Blocks.STONE ).nonOpaque() ) );

    public static void init()
    {

    }

    @Environment( EnvType.CLIENT )
    public static void registerRenderLayers()
    {
        BlockRenderLayerMap.INSTANCE.putBlocks( RenderLayer.getTranslucent(), FUNNEL );
    }

    private static BlockItem createBlockItem( Block block )
    {
        return createBlockItem( block, Conveyance.generalItemGroup );
    }

    private static BlockItem createBlockItem( Block block, ItemGroup group )
    {
        return new BlockItem( block, new Item.Settings().group( group ) );
    }

    public static <T extends Block> T register( String name, T block )
    {
        Registry.register( Registry.BLOCK, Conveyance.id( name ), block );
        Registry.register( Registry.ITEM, Registry.BLOCK.getId( block ), createBlockItem( block ) );

        if( block instanceof Conveyor )
        {
            ((Conveyor) block).setId( Conveyance.id( name ) );
        }

        return block;
    }

    public static <T extends Block> T register( String name, T block, BlockItem blockItem )
    {
        Registry.register( Registry.BLOCK, Conveyance.id( name ), block );
        Registry.register( Registry.ITEM, Conveyance.id( name ), blockItem );

        return block;
    }

    public static <T extends Block> T register( String name, T block, String itemName, BlockItem blockItem )
    {
        Registry.register( Registry.BLOCK, Conveyance.id( name ), block );
        Registry.register( Registry.ITEM, Conveyance.id( itemName ), blockItem );

        return block;
    }
}
