package com.jemmazz.conveyaway.client.renderers;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyaway.blocks.entities.FunnelBlockEntity;
import com.jemmazz.conveyaway.blocks.entities.VerticalConveyorBlockEntity;
import com.jemmazz.conveyaway.client.ConveyorSyncHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class FunnelBlockEntityRenderer implements BlockEntityRenderer<FunnelBlockEntity>
{
    @Override
    public void render( FunnelBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay )
    {
        Conveyor funnel = (Conveyor) blockEntity.getCachedState().getBlock();
        double speed = funnel.getSpeed();

        matrices.push();
        if( !blockEntity.isEmpty() )
        {
            double position = blockEntity.getPosition() / (speed * 1.0F);
            double prevPosition = blockEntity.getPrevPosition() / (speed * 1.0F);
            float deltaPosition = (float) MathHelper.lerp( tickDelta, prevPosition, position );

            matrices.translate( 0.5, 12.0 / 16.0, 0.5 );
            matrices.translate( 0, -deltaPosition, 0 );
            MinecraftClient.getInstance().getItemRenderer().renderItem( blockEntity.getStack(), ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0 );
        }
        matrices.pop();
    }
}
