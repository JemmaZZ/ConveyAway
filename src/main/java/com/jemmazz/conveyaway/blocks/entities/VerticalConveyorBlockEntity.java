package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VerticalConveyorBlockEntity extends ConveyorBlockEntity implements BlockEntityClientSerializable, SingularStackInventory {
    protected int verticalPosition = 0;
    protected int prevVerticalPosition = 0;

    public VerticalConveyorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ConveyAwayBlockEntities.VERTICAL_CONVEYOR, blockPos, blockState);
    }

    public static void tick(World world, BlockPos pos, BlockState state, VerticalConveyorBlockEntity be) {
        be.tick(world, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        Direction facing = getCachedState().get(Properties.HORIZONTAL_FACING);

        if (!isEmpty() && getCachedState().get(Conveyor.TOP) && world.getBlockEntity(pos.offset(Direction.UP)) instanceof VerticalConveyorBlockEntity nextConveyor) {
            if (nextConveyor.isEmpty() || nextConveyor.verticalPosition > 0) {
                if (!getCachedState().get(Conveyor.BACK) && verticalPosition < getSpeed() || getCachedState().get(Conveyor.BACK) && verticalPosition < (getSpeed() - getSpeed() / 4)) {
                    prevVerticalPosition = verticalPosition;
                    verticalPosition += 1;
                } else {
                    prevVerticalPosition = -1;
                    verticalPosition = 0;
                    nextConveyor.give(getStack());
                    clear();
                    if (!world.isClient())
                        sendPacket((ServerWorld) world, writeNbt(new NbtCompound()));
                }
            } else {
                prevVerticalPosition = 0;
                verticalPosition = 0;
                prevPosition = 0;
                position = 0;
            }
        } else {
            prevVerticalPosition = 0;
            verticalPosition = 0;
            position = 0;
            prevPosition = 0;
        }
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }

    public void setVerticalPosition(int verticalPosition) {
        this.verticalPosition = verticalPosition;
    }

    public int getPrevVerticalPosition() {
        return prevVerticalPosition;
    }

    public void setPrevVerticalPosition(int prevVerticalPosition) {
        this.prevVerticalPosition = prevVerticalPosition;
    }

    public void give(ItemStack stack) {
        if (getCachedState().get(Conveyor.TOP))
            prevVerticalPosition = -1;
        else
            prevVerticalPosition = 0;
        verticalPosition = 0;
        prevPosition = 0;
        position = 0;
        setStack(stack);
        if (!world.isClient())
            sendPacket((ServerWorld) world, writeNbt(new NbtCompound()));
    }

    @Override
    protected void sendPacket(ServerWorld w, NbtCompound tag) {
        tag.putString("id", BlockEntityType.getId(getType()).toString());
        sendPacket(w, new BlockEntityUpdateS2CPacket(getPos(), 127, tag));
    }

    @Override
    protected void sendPacket(ServerWorld world, BlockEntityUpdateS2CPacket packet) {
        world.getPlayers(player -> player.squaredDistanceTo(Vec3d.of(getPos())) < 40 * 40).forEach(player -> player.networkHandler.sendPacket(packet));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("verticalPosition", verticalPosition);
        nbt.putInt("prevVerticalPosition", prevVerticalPosition);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        verticalPosition = nbt.getInt("verticalPosition");
        prevVerticalPosition = nbt.getInt("prevVerticalPosition");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return writeNbt(new NbtCompound());
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }
}
