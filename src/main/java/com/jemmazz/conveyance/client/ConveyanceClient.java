package com.jemmazz.conveyance.client;

import com.jemmazz.conveyance.Conveyance;
import com.jemmazz.conveyance.client.renderers.ConveyorBlockEntityRenderer;
import com.jemmazz.conveyance.client.renderers.FunnelBlockEntityRenderer;
import com.jemmazz.conveyance.client.renderers.InserterBlockEntityRenderer;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import com.jemmazz.conveyance.init.ConveyanceBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;

public class ConveyanceClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModelLoadingRegistry.INSTANCE.registerModelProvider( ( resourceManager, consumer ) -> {
            consumer.accept( new ModelIdentifier( Conveyance.id( "roller" ), "" ) );
            consumer.accept( new ModelIdentifier( Conveyance.id( "supports" ), "" ) );
        } );

        ConveyanceBlocks.registerRenderLayers();
        BlockEntityRendererRegistry.register( ConveyanceBlockEntities.CONVEYOR, ctx -> new ConveyorBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyanceBlockEntities.VERTICAL_CONVEYOR, ctx -> new ConveyorBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyanceBlockEntities.FUNNEL, ctx -> new FunnelBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyanceBlockEntities.INSERTER, ctx -> new InserterBlockEntityRenderer() );

        ConveyorSyncHandler.init();
    }
}
