package com.jemmazz.conveyaway.client;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.client.renderers.ConveyorBlockEntityRenderer;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import com.jemmazz.conveyaway.init.ConveyAwayBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ConveyAwayClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, consumer) -> {
            consumer.accept(new ModelIdentifier(new Identifier(ConveyAway.MODID, "roller"), ""));
        });

        ConveyAwayBlocks.registerRenderLayers();

		BlockEntityRendererRegistry.INSTANCE.register(ConveyAwayBlockEntities.CONVEYOR, (blockEntityRenderDispatcher -> new ConveyorBlockEntityRenderer()));

        ConveyorSyncHandler.init();
    }
}
