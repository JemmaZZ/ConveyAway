package com.jemmazz.conveyance.client.renderers;

import com.jemmazz.conveyance.Conveyance;
import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyance.blocks.entities.VerticalConveyorBlockEntity;
import com.jemmazz.conveyance.client.ConveyorSyncHandler;
import net.minecraft.block.Block;
import net.minecraft.block.enums.DoubleBlockHalf;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.item.TallBlockItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class ConveyorBlockEntityRenderer implements BlockEntityRenderer<ConveyorBlockEntity>
{
    public static void renderBelt( ConveyorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay )
    {
        Conveyor conveyor = (Conveyor) blockEntity.getCachedState().getBlock();
        Direction facing = blockEntity.getCachedState().get( Properties.HORIZONTAL_FACING );
        boolean back = blockEntity.getCachedState().get( Conveyor.BACK );
        boolean front = conveyor.isFlat() ? blockEntity.getCachedState().get( Conveyor.FRONT ) : blockEntity.getCachedState().get( Conveyor.TOP );
        boolean vertFront = !conveyor.isFlat() ? blockEntity.getCachedState().get( Conveyor.FRONT ) : false;
        double speed = conveyor.getSpeed();
        Identifier identifier = conveyor.getId();
        double position = ConveyorSyncHandler.position / speed;
        double prevPosition = ConveyorSyncHandler.prevPosition / speed;
        double distance = blockEntity.getPos().getSquaredDistance( MinecraftClient.getInstance().player.getBlockPos() );

        matrices.push();
        VertexConsumer vertices = vertexConsumers.getBuffer( RenderLayer.getEntitySolid( new Identifier( identifier.getNamespace() + ":textures/block/" + identifier.getPath() + ".png" ) ) );
        Matrix4f modelMatrix = matrices.peek().getPositionMatrix();

        double deltaPosition = MathHelper.lerp( tickDelta, prevPosition, position );

        float length = 1;
        float height = 0.5F;
        float width = 1;

        if( !conveyor.isFlat() && vertFront )
        {
            length = 0.5F;
        }

        if( conveyor.isFlat() || !conveyor.isFlat() && blockEntity.getCachedState().get( Conveyor.BACK ) )
        {
            matrices.translate( 0.5, 0, 0.5 );
            matrices.multiply( Vec3f.POSITIVE_Y.getDegreesQuaternion( facing.asRotation() ) );
            if( facing == Direction.NORTH || facing == Direction.SOUTH )
            {
                matrices.multiply( Vec3f.POSITIVE_Y.getDegreesQuaternion( 180 ) );
            }
            matrices.translate( -0.5, 0, -0.5 );

            matrices.translate( 0, 0, 1 - length );

            // top
            if( distance < 2500 )
            {
                matrices.translate( 0, 0.0001, 0 );
                vertices.vertex( modelMatrix, 0, height, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, height, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, height, length ).color( 255, 255, 255, 255 ).texture( width, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, height, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                matrices.translate( 0, -0.0001, 0 );
            }

            if( distance < 1200 )
            {
                // bottom
                matrices.translate( 0, -0.0001, 0 );
                vertices.vertex( modelMatrix, width, 0, 0 ).color( 255, 255, 255, 255 ).texture( -width + 1.5F, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, 0, length ).color( 255, 255, 255, 255 ).texture( -width + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, 0, length ).color( 255, 255, 255, 255 ).texture( 0, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, 0, 0 ).color( 255, 255, 255, 255 ).texture( 0, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                matrices.translate( 0, 0.0001, 0 );
            }

            if( height != 0 )
            {
                if( !back )
                {
                    matrices.translate( 0, 0, 0.0001 );
                    // north
                    vertices.vertex( modelMatrix, width, height, length ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, height, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, 0, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, width, 0, length ).color( 255, 255, 255, 255 ).texture( width, (float) (height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    matrices.translate( 0, 0, -0.0001 );
                }

                if( !front )
                {
                    matrices.translate( 0, 0, -0.0001 );
                    // south
                    vertices.vertex( modelMatrix, width, 0, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (-height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, 0, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (-height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, height, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, width, height, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    matrices.translate( 0, 0, 0.0001 );
                }
            }

            matrices.push();
            if( distance < 1800 )
            {
                ModelPart.Cuboid cuboid = new ModelPart.Cuboid( 8, 21, 0F, 0F, 0F, 16.0F, 3.0F, 3.0F, 0, 0, 0, false, 16, 16 );
                matrices.translate( 0, 2.5f / 16f, 2.5F / 16F );
                matrices.translate( 0, 1.5F / 16, 1.5F / 16 );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (deltaPosition * 180) ) );
                matrices.translate( 0, -1.5F / 16, -1.5F / 16 );
                cuboid.renderCuboid( matrices.peek(), vertices, light, overlay, 1, 1, 1, 1 );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (-deltaPosition * 180) ) );

                matrices.translate( 0, 0, 8F / 16F );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (deltaPosition * 180) ) );
                cuboid.renderCuboid( matrices.peek(), vertices, light, overlay, 1, 1, 1, 1 );
            }
            matrices.pop();
        }

        if( !conveyor.isFlat() )
        {
            matrices.translate( 0.5, 0, 0.5 );
            if( !blockEntity.getCachedState().get( Conveyor.BACK ) )
            {
                matrices.multiply( Vec3f.POSITIVE_Y.getDegreesQuaternion( facing.asRotation() ) );
                if( facing == Direction.NORTH || facing == Direction.SOUTH )
                {
                    matrices.multiply( Vec3f.POSITIVE_Y.getDegreesQuaternion( 180 ) );
                }
            }
            matrices.translate( -0.5, 0, -0.5 );

            matrices.multiply( Vec3f.POSITIVE_X.getDegreesQuaternion( 90 ) );
            matrices.translate( 0, 0, -1 );

            matrices.translate( 0, 0, 1 - length );

            // top
            if( distance < 2500 )
            {
                matrices.translate( 0, 0.0001, 0 );
                vertices.vertex( modelMatrix, 0, height, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, height, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, height, length ).color( 255, 255, 255, 255 ).texture( width, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, height, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                matrices.translate( 0, -0.0001, 0 );
            }

            if( distance < 1200 )
            {
                // bottom
                matrices.translate( 0, -0.0001, 0 );
                vertices.vertex( modelMatrix, width, 0, 0 ).color( 255, 255, 255, 255 ).texture( -width + 1.5F, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, width, 0, length ).color( 255, 255, 255, 255 ).texture( -width + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, 0, length ).color( 255, 255, 255, 255 ).texture( 0, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                vertices.vertex( modelMatrix, 0, 0, 0 ).color( 255, 255, 255, 255 ).texture( 0, (float) (length + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                matrices.translate( 0, 0.0001, 0 );
            }

            if( height != 0 )
            {
                if( !back )
                {
                    matrices.translate( 0, 0, 0.0001 );
                    // north
                    vertices.vertex( modelMatrix, width, height, length ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, height, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, 0, length ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, width, 0, length ).color( 255, 255, 255, 255 ).texture( width, (float) (height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    matrices.translate( 0, 0, -0.0001 );
                }

                if( !front )
                {
                    matrices.translate( 0, 0, -0.0001 );
                    // south
                    vertices.vertex( modelMatrix, width, 0, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (-height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, 0, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (-height + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, 0, height, 0 ).color( 255, 255, 255, 255 ).texture( 0 + 1.5F, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    vertices.vertex( modelMatrix, width, height, 0 ).color( 255, 255, 255, 255 ).texture( width, (float) (0 + deltaPosition) ).overlay( OverlayTexture.DEFAULT_UV ).light( light ).normal( 0, 0, 1 ).next();
                    matrices.translate( 0, 0, 0.0001 );
                }
            }

            if( distance < 1800 )
            {
                ModelPart.Cuboid cuboid = new ModelPart.Cuboid( 8, 21, 0F, 0F, 0F, 16.0F, 3.0F, 3.0F, 0, 0, 0, false, 16, 16 );
                matrices.translate( 0, 2.5f / 16f, 2.5F / 16F );
                matrices.translate( 0, 1.5F / 16, 1.5F / 16 );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (deltaPosition * 180) ) );
                matrices.translate( 0, -1.5F / 16, -1.5F / 16 );
                cuboid.renderCuboid( matrices.peek(), vertices, light, overlay, 1, 1, 1, 1 );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (-deltaPosition * 180) ) );

                matrices.translate( 0, 0, 8F / 16F );
                matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( (float) (deltaPosition * 180) ) );
                cuboid.renderCuboid( matrices.peek(), vertices, light, overlay, 1, 1, 1, 1 );
            }
        }
        matrices.pop();
    }

    @Override
    public void render( ConveyorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay )
    {
        Conveyor conveyor = (Conveyor) blockEntity.getCachedState().getBlock();
        Direction facing = blockEntity.getCachedState().get( Properties.HORIZONTAL_FACING );
        double speed = conveyor.getSpeed();

        renderBelt( blockEntity, tickDelta, matrices, vertexConsumers, light, overlay );

        matrices.push();
        if( !blockEntity.isEmpty() )
        {
            double position = blockEntity.getPosition() / (speed * 1.0F);
            double prevPosition = blockEntity.getPrevPosition() / (speed * 1.0F);
            double deltaPosition = (prevPosition * (1.0 - tickDelta)) +  (position * tickDelta);

            matrices.translate( 0.5, 0, 0.5 );
            matrices.multiply( Vec3f.NEGATIVE_Y.getDegreesQuaternion( facing.asRotation() ) );

            if( conveyor.isFlat() || !conveyor.isFlat() && blockEntity.getCachedState().get( Conveyor.BACK ) )
            {
                matrices.translate( 0, 12.0 / 16.0, 0 );
            }
            else
            {
                matrices.translate( 0, 8.0 / 16.0, 0 );
            }

            if( !conveyor.isFlat() )
            {
                matrices.translate( 0, 0, -0.25 );
            }

            if( !conveyor.isFlat() )
            {
                double verticalPosition = ((VerticalConveyorBlockEntity) blockEntity).getVerticalPosition() / (speed * 1.0f);
                double prevVerticalPosition = ((VerticalConveyorBlockEntity) blockEntity).getPrevVerticalPosition() / (speed * 1.0f);
                double verticalDeltaPosition = MathHelper.lerp( tickDelta, prevVerticalPosition, verticalPosition );

                matrices.translate( 0, verticalDeltaPosition, deltaPosition );
                matrices.push();
                if( !(blockEntity.getStack().getItem() instanceof BlockItem) || blockEntity.getStack().getItem() == Items.REDSTONE )
                {
                    matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( 90 ) );
                    matrices.translate( 0, 0, -3.5 / 16f );

                    matrices.scale( 0.625F, 0.625F, 0.625F );

                    MinecraftClient.getInstance().getItemRenderer().renderItem( blockEntity.getStack(), ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0 );
                }
                else
                {
                    Block block = Block.getBlockFromItem( blockEntity.getStack().getItem() );

                    matrices.scale( 0.5F, 0.5F, 0.5F );

                    matrices.translate( -0.5F, -0.5F, -0.5F );
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity( block.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV );
                    if( blockEntity.getStack().getItem() instanceof TallBlockItem )
                    {
                        matrices.translate( 0, 1, 0 );
                        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity( block.getDefaultState().with( Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER ), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV );
                    }
                }
                matrices.pop();
                matrices.translate( -0.5, -1.25, -0.5 );
                VertexConsumer vertices = vertexConsumers.getBuffer( RenderLayer.getEntitySolid( Conveyance.id( "textures/block/supports.png" ) ) );
                BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel( new ModelIdentifier( Conveyance.id( "supports" ), "" ) );
                MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render( matrices.peek(), vertices, null, model, blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ(), light, OverlayTexture.DEFAULT_UV );
            }
            else
            {
                matrices.translate( 0, 0, deltaPosition );

                if( !(blockEntity.getStack().getItem() instanceof BlockItem) || blockEntity.getStack().getItem() == Items.REDSTONE )
                {
                    matrices.multiply( Vec3f.NEGATIVE_X.getDegreesQuaternion( 90 ) );
                    matrices.translate( 0, 0, -3.5 / 16f );

                    matrices.scale( 0.625F, 0.625F, 0.625F );

                    MinecraftClient.getInstance().getItemRenderer().renderItem( blockEntity.getStack(), ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0 );
                }
                else
                {
                    Block block = Block.getBlockFromItem( blockEntity.getStack().getItem() );

                    matrices.scale( 0.5F, 0.5F, 0.5F );

                    matrices.translate( -0.5F, -0.5F, -0.5F );
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity( block.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV );
                    if( blockEntity.getStack().getItem() instanceof TallBlockItem )
                    {
                        matrices.translate( 0, 1, 0 );
                        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity( block.getDefaultState().with( Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER ), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV );
                    }
                }
            }
        }
        matrices.pop();
    }
}
