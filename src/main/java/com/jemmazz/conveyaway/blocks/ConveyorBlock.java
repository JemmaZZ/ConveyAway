package com.jemmazz.conveyaway.blocks;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import com.jemmazz.conveyaway.utilities.MovementUtilities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ConveyorBlock extends BlockWithEntity implements BlockEntityProvider, Conveyor {
    private final int speed;
    private Identifier id;

    public ConveyorBlock(Settings settings, int speed) {
        super(settings);

        this.speed = speed;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void setId(Identifier id) {
        this.id = id;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ConveyAwayBlockEntities.CONVEYOR, (world1, pos, state1, be) -> ConveyorBlockEntity.tick(world1, pos, state1, (ConveyorBlockEntity) be));
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
//        return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
        return 0;
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        BlockPos pos = new BlockPos(entity.getPos());

        if (!entity.isOnGround() || (entity.getY() - blockPos.getY()) != (8F / 16F))
            return;

        if (entity instanceof PlayerEntity && entity.isSneaking())
            return;

        Direction direction = blockState.get(Properties.HORIZONTAL_FACING);

        if (entity instanceof ItemEntity && pos.equals(blockPos) && world.getBlockEntity(blockPos) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

//            if (blockEntity.isEmpty()) {
//                blockEntity.setStack(((ItemEntity) entity).getStack());
//                entity.remove();
//            }
        } else if (!(entity instanceof ItemEntity)) {
            MovementUtilities.pushEntity(entity, blockPos, 2.0F / getSpeed(), direction);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add(Properties.HORIZONTAL_FACING, Conveyor.BACK, Conveyor.FRONT);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = state;
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        if (world.getBlockState(pos.offset(facing)).getBlock() instanceof Conveyor) {
            newState = newState.with(Conveyor.FRONT, true);
        } else {
            newState = newState.with(Conveyor.FRONT, false);
        }

        if (world.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof Conveyor) {
            newState = newState.with(Conveyor.BACK, true);
        } else {
            newState = newState.with(Conveyor.BACK, false);
        }
        return newState;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World world = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        BlockState newState = this.getDefaultState().with(Properties.HORIZONTAL_FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());

        newState = newState.getStateForNeighborUpdate(null, newState, world, blockPos, blockPos);

        return newState;
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext entityContext) {
        VoxelShape conveyor = VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
        return conveyor;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
