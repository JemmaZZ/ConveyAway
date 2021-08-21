package com.jemmazz.conveyaway.utilities;

import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class MovementUtilities {
    /**
     * Rotates a VoxelShape
     * @param from  Original Direction
     * @param to    Target-direction
     * @param shape A valid VoxelShape
     * @return VoxelShape
     */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combine(buffer[1], VoxelShapes.cuboid(1-maxZ, minY, minX, 1-minZ, maxY, maxX), BooleanBiFunction.OR));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }

    public static void pushEntity(Entity entity, BlockPos pos, float speed, Direction facing) {
        pushEntity(entity, pos, speed, facing, true);
    }

    public static void pushEntity(Entity entity, BlockPos pos, float speed, Direction facing, boolean shouldCenter) {
        double motionX = entity.getVelocity().getX();
        double motionZ = entity.getVelocity().getZ();

        if (speed * facing.getOffsetX() > 0 && motionX < speed) {
            entity.addVelocity(speed / 2, 0, 0);
        } else if (speed * facing.getOffsetX() < 0 && motionX > -speed) {
            entity.addVelocity(-speed / 2, 0, 0);
        }

        if (speed * facing.getOffsetZ() > 0 && motionZ < speed) {
            entity.addVelocity(0, 0, speed / 2);
        } else if (speed * facing.getOffsetZ() < 0 && motionZ > -speed) {
            entity.addVelocity(0, 0, -speed / 2);
        }

        if (shouldCenter) {
            centerEntity(entity, pos, speed, facing);
        }
    }

    private static void centerEntity(Entity entity, BlockPos pos, float speed, Direction facing) {
        if (speed * facing.getOffsetX() > 0 || speed * facing.getOffsetX() < 0) {
            centerZ(entity, pos);
        }

        if (speed * facing.getOffsetZ() > 0 || speed * facing.getOffsetZ() < 0) {
            centerX(entity, pos);
        }
    }

    private static void centerZ(Entity entity, BlockPos pos) {
        if (entity.getZ() > pos.getZ() + .55) {
            entity.addVelocity(0, 0, -0.1F);
        } else if (entity.getZ() < pos.getZ() + .45) {
            entity.addVelocity(0, 0, 0.1F);
        } else {
            entity.setVelocity(entity.getVelocity().getX(), entity.getVelocity().getY(), 0);
        }
    }

    private static void centerX(Entity entity, BlockPos pos) {
        if (entity.getX() > pos.getX() + .55) {
            entity.addVelocity(-0.1F, 0, 0);
        } else if (entity.getX() < pos.getX() + .45) {
            entity.addVelocity(0.1F, 0, 0);
        } else {
            entity.setVelocity(0, entity.getVelocity().getY(), entity.getVelocity().getZ());
        }
    }
}