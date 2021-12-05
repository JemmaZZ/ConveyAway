package com.jemmazz.conveyance.client.renderers;

import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.entities.FunnelBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

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
