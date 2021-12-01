package com.jemmazz.conveyaway.client.renderers;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.blocks.InserterBlock;
import com.jemmazz.conveyaway.blocks.entities.InserterBlockEntity;
import com.jemmazz.conveyaway.client.models.InserterArmModel;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class InserterBlockEntityRenderer implements BlockEntityRenderer<InserterBlockEntity>
{

	@Override
    public void render(InserterBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Direction direction = blockEntity.getCachedState().get(HorizontalFacingBlock.FACING);
		String type = ((InserterBlock) blockEntity.getCachedState().getBlock()).getType();
		int speed = ((InserterBlock) blockEntity.getCachedState().getBlock()).getSpeed();
    	InserterArmModel modelInserterArm = new InserterArmModel();

		float position = blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * tickDelta;

		matrices.push();
		matrices.translate(0.5, 1.5, 0.5);
		matrices.multiply( Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
		if (direction == Direction.SOUTH) {
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
		} else if (direction == Direction.EAST) {
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
		} else if (direction == Direction.WEST) {
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
		}

		modelInserterArm.getLowerArm().yaw = (float) Math.toRadians((position / speed) * 180F);

		if (position < speed / 4f) {
			modelInserterArm.getLowerArm().pitch = (float) Math.toRadians((position / (speed / 4f)) * -30F + 40F);
			modelInserterArm.getMiddleArm().pitch = (float) Math.toRadians((position / (speed / 4f)) * -20F + 40F);
		} else if (position >= speed - (speed / 4f) && position < speed) {
			modelInserterArm.getLowerArm().pitch = (float) Math.toRadians((position / speed) * 120F - 80F);
			modelInserterArm.getMiddleArm().pitch = (float) Math.toRadians((position / speed) * 80F - 40F);
		} else {
			modelInserterArm.getLowerArm().pitch = (float) Math.toRadians(10);
			modelInserterArm.getMiddleArm().pitch = (float) Math.toRadians(20);
		}

		modelInserterArm.getLowerArm().pitch += (float) Math.toRadians( -10 );
		modelInserterArm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(new Identifier("conveyaway:textures/block/inserter_" + type + ".png"))), light, overlay, 1f, 1f, 1f, 1f);
		matrices.pop();

		if (!blockEntity.isEmpty()) {
			matrices.push();
			matrices.translate(0.5, 0, 0.5);
			if (direction == Direction.NORTH) {
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
			} else if (direction == Direction.EAST) {
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
			} else if (direction == Direction.WEST) {
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
			}

			float lowArmSize = 8 / 16F;
			float midArmSize = 10 / 16F;
			float connectingAngle = modelInserterArm.getMiddleArm().pitch;

			float distance = (float) Math.sqrt(Math.pow(lowArmSize, 2) + Math.pow(midArmSize, 2) - 2 * lowArmSize * midArmSize * Math.cos(connectingAngle));
			float angle = (float) (180 - Math.toDegrees(modelInserterArm.getLowerArm().pitch + modelInserterArm.getMiddleArm().pitch));

			matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) Math.toDegrees(modelInserterArm.getLowerArm().yaw)));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180 + angle));
			matrices.translate(0, (1 + 5 / 16F) - distance, distance - (1F / 16F));
			matrices.scale(0.3F, 0.3F, 0.3F);

			MinecraftClient.getInstance().getItemRenderer().renderItem( blockEntity.getStack(), ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0 );
			matrices.pop();
		}
    }
}