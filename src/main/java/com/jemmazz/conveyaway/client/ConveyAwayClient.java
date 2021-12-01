package com.jemmazz.conveyaway.client;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.client.renderers.ConveyorBlockEntityRenderer;
import com.jemmazz.conveyaway.client.renderers.FunnelBlockEntityRenderer;
import com.jemmazz.conveyaway.client.renderers.InserterBlockEntityRenderer;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import com.jemmazz.conveyaway.init.ConveyAwayBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ConveyAwayClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModelLoadingRegistry.INSTANCE.registerModelProvider( ( resourceManager, consumer ) -> {
            consumer.accept( new ModelIdentifier( new Identifier( ConveyAway.MODID, "roller" ), "" ) );
            consumer.accept( new ModelIdentifier( new Identifier( ConveyAway.MODID, "supports" ), "" ) );
        } );

        ConveyAwayBlocks.registerRenderLayers();
        BlockEntityRendererRegistry.register( ConveyAwayBlockEntities.CONVEYOR, ctx -> new ConveyorBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyAwayBlockEntities.VERTICAL_CONVEYOR, ctx -> new ConveyorBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyAwayBlockEntities.FUNNEL, ctx -> new FunnelBlockEntityRenderer() );
        BlockEntityRendererRegistry.register( ConveyAwayBlockEntities.INSERTER, ctx -> new InserterBlockEntityRenderer() );

        ConveyorSyncHandler.init();
    }
}
