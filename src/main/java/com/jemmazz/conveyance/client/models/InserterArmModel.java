// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.17
// Paste this class into your mod and generate all required imports

package com.jemmazz.conveyance.client.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class InserterArmModel extends EntityModel<Entity>
{
    private final ModelPart lowerArm;
    private final ModelPart middleArm;
    private final ModelPart topArm;

    public InserterArmModel()
    {
        ModelData modelPartData = getModelData();

        lowerArm = modelPartData.getRoot().getChild( "lowerArm" ).createPart( 16, 16 );
        middleArm = lowerArm.getChild( "middleArm" );
        topArm = middleArm.getChild( "topArm" );
    }

    public static ModelData getModelData()
    {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData lowerArm = modelPartData.addChild( "lowerArm", ModelPartBuilder.create().uv( 0, 0 ).cuboid( 0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false )
            .uv( 4, 0 ).cuboid( -1.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false ), ModelTransform.of( 0.0F, 23.0F, 0.0F, 0.0F, 0.0F, 0.0F ) );

        ModelPartData middleArm = lowerArm.addChild( "middleArm", ModelPartBuilder.create().uv( 8, 0 ).cuboid( -0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false ), ModelTransform.of( 0.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.0F ) );

        ModelPartData topArm = middleArm.addChild( "topArm", ModelPartBuilder.create().uv( 0, 13 ).cuboid( -1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation( 0.0F ) ).mirrored( false )
            .uv( 8, 13 ).cuboid( -0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false )
            .uv( 0, 11 ).cuboid( -1.5F, -3.0F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false )
            .uv( 8, 10 ).cuboid( 1.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false )
            .uv( 12, 13 ).cuboid( -2.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation( 0.0F ) ).mirrored( false ), ModelTransform.of( 0.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F ) );
        return modelData;
    }

    public static TexturedModelData getTexturedModelData()
    {
        return TexturedModelData.of( getModelData(), 16, 16 );
    }

    public ModelPart getLowerArm()
    {
        return lowerArm;
    }

    public ModelPart getMiddleArm()
    {
        return middleArm;
    }

    public ModelPart getTopArm()
    {
        return topArm;
    }

    @Override
    public void setAngles( Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch )
    {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render( MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha )
    {
        lowerArm.render( matrixStack, buffer, packedLight, packedOverlay );
    }

    public void setRotationAngle( ModelPart bone, float x, float y, float z )
    {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

}