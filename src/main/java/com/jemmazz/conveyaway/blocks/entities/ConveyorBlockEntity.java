package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.blocks.ConveyorBlock;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConveyorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
    public int position = 0;
    public int prevPosition = 0;

    public ConveyorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ConveyAwayBlockEntities.CONVEYOR, blockPos, blockState);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ConveyorBlockEntity be) {

    }

    @Override
    public void fromClientTag(NbtCompound tag) {

    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return null;
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return null;
    }
}
