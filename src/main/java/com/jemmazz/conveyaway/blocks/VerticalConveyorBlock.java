package com.jemmazz.conveyaway.blocks;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.blocks.entities.VerticalConveyorBlockEntity;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import com.jemmazz.conveyaway.utilities.MovementUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
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

public class VerticalConveyorBlock extends ConveyorBlock {
    public VerticalConveyorBlock(Settings settings, int speed) {
        super(settings, speed);
    }

    @Override
    public boolean isFlat() {
        return false;
    }

    @Override
    public Identifier getId() {
        Identifier id = super.getId();
        return new Identifier(id.getNamespace(), id.getPath().split("_")[1]);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VerticalConveyorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ConveyAwayBlockEntities.VERTICAL_CONVEYOR, (world1, pos, state1, be) -> VerticalConveyorBlockEntity.tick(world1, pos, state1, (VerticalConveyorBlockEntity) be));
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add(Properties.HORIZONTAL_FACING, Conveyor.BACK, Conveyor.TOP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = state;
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        if (world.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof Conveyor && facing == world.getBlockState(pos.offset(facing.getOpposite())).get(Properties.HORIZONTAL_FACING) && ((Conveyor) world.getBlockState(pos.offset(facing.getOpposite())).getBlock()).isFlat()) {
            newState = newState.with(Conveyor.BACK, true);
        } else {
            newState = newState.with(Conveyor.BACK, false);
        }

        if (world.getBlockState(pos.offset(Direction.UP)).getBlock() instanceof Conveyor && facing == world.getBlockState(pos.offset(Direction.UP)).get(Properties.HORIZONTAL_FACING) && !((Conveyor) world.getBlockState(pos.offset(Direction.UP)).getBlock()).isFlat()) {
            newState = newState.with(Conveyor.TOP, true);
        } else {
            newState = newState.with(Conveyor.TOP, false);
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
        VoxelShape conveyor = VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1);
        VoxelShape conveyor_connect = VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
        return blockState.get(Conveyor.BACK) ? MovementUtilities.rotateShape(Direction.NORTH, blockState.get(Properties.HORIZONTAL_FACING).getOpposite(), VoxelShapes.union(conveyor, conveyor_connect)) : MovementUtilities.rotateShape(Direction.NORTH, blockState.get(Properties.HORIZONTAL_FACING).getOpposite(), conveyor);
    }
}
