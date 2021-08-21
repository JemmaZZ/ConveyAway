package com.jemmazz.conveyaway.client.renderers;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.client.ConveyorSyncHandler;
import com.jemmazz.conveyaway.blocks.entities.ConveyorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class ConveyorBlockEntityRenderer implements BlockEntityRenderer<ConveyorBlockEntity> {
    @Override
    public void render(ConveyorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Conveyor conveyor = (Conveyor) blockEntity.getCachedState().getBlock();
        Direction facing = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        boolean back = blockEntity.getCachedState().get(Conveyor.BACK);
        boolean front = blockEntity.getCachedState().get(Conveyor.FRONT);
        int speed = conveyor.getSpeed();
        Identifier identifier = conveyor.getId();
        float position = ConveyorSyncHandler.position / speed;
        float prevPosition = ConveyorSyncHandler.prevPosition / speed;

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(facing.asRotation()));
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        }
        matrices.translate(-0.5, 0, -0.5);
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(new Identifier(identifier.getNamespace() + ":textures/block/" + identifier.getPath() + ".png")));
        Matrix4f modelMatrix = matrices.peek().getModel();

        float deltaPosition = MathHelper.lerp(tickDelta, prevPosition, position);

        float length = 1;
        float height = 0.5F;
        float width = 1;
        matrices.translate(0, 0, 1 - length);

        // top
        matrices.translate(0, 0.0001, 0);
        vertices.vertex(modelMatrix, 0, height, 0).color(255, 255, 255, 255).texture(0 + 1.5F, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, 0, height, length).color(255, 255, 255, 255).texture(0 + 1.5F, length + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, width, height, length).color(255, 255, 255, 255).texture(width, length + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, width, height, 0).color(255, 255, 255, 255).texture(width, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        matrices.translate(0, -0.0001, 0);

        // bottom
        matrices.translate(0, -0.0001, 0);
        vertices.vertex(modelMatrix, width, 0, 0).color(255, 255, 255, 255).texture(-width + 1.5F, length + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, width, 0, length).color(255, 255, 255, 255).texture(-width + 1.5F, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, 0, 0, length).color(255, 255, 255, 255).texture(0, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        vertices.vertex(modelMatrix, 0, 0, 0).color(255, 255, 255, 255).texture(0, length + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,0,1).next();
        matrices.translate(0, 0.0001, 0);

        if (height != 0) {
            if (!back) {
                matrices.translate(0, 0, 0.0001);
                // north
                vertices.vertex(modelMatrix, width, height, length).color(255, 255, 255, 255).texture(width, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, 0, height, length).color(255, 255, 255, 255).texture(0 + 1.5F, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, 0, 0, length).color(255, 255, 255, 255).texture(0 + 1.5F, height + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, width, 0, length).color(255, 255, 255, 255).texture(width, height + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                matrices.translate(0, 0, -0.0001);
            }

            if (!front) {
                matrices.translate(0, 0, -0.0001);
                // south
                vertices.vertex(modelMatrix, width, 0, 0).color(255, 255, 255, 255).texture(width, -height + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, 0, 0, 0).color(255, 255, 255, 255).texture(0 + 1.5F, -height + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, 0, height, 0).color(255, 255, 255, 255).texture(0 + 1.5F, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                vertices.vertex(modelMatrix, width, height, 0).color(255, 255, 255, 255).texture(width, 0 + deltaPosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 0, 1).next();
                matrices.translate(0, 0, 0.0001);
            }
        }

        //VertexConsumer verticesRoller = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(ConveyAway.id("textures/block/conveyor_metal.png")));
        //VertexConsumer verticesRoller = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(ConveyAway.id("textures/block/conveyor_metal.png")));

        ModelPart.Cuboid cuboid = new ModelPart.Cuboid(8, 21, 0F, 0F, 0F, 16.0F, 3.0F, 3.0F,0,0,0,false,16,16);
        matrices.translate(0, 2.5f / 16f, 2.5F / 16F);
        matrices.translate(0, 1.5F / 16, 1.5F / 16);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(deltaPosition * 180));
        matrices.translate(0, -1.5F / 16, -1.5F / 16);
        cuboid.renderCuboid(matrices.peek(), vertices, light, overlay, 1, 1, 1, 1);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(-deltaPosition * 180));

        matrices.translate(0, 0, 8F / 16F);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(deltaPosition * 180));
        cuboid.renderCuboid(matrices.peek(), vertices, light, overlay, 1, 1, 1, 1);
        matrices.pop();
    }
}
