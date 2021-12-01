package com.jemmazz.conveyaway.blocks;

import com.jemmazz.conveyaway.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyaway.blocks.entities.InserterBlockEntity;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InserterBlock extends BlockWithEntity implements BlockEntityProvider {
	private String type;
	private int speed;

    public InserterBlock(String type, int speed, Settings settings) {
        super(settings);

        this.type = type;
        this.speed = speed;
    }

	public String getType() {
		return type;
	}

	public int getSpeed() {
    	return speed;
	}

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InserterBlockEntity( pos, state );
    }

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker( World world, BlockState state, BlockEntityType<T> type )
	{
		return checkType( type, ConveyAwayBlockEntities.INSERTER, ( world1, pos, state1, be ) -> InserterBlockEntity.tick( world1, pos, state1, (InserterBlockEntity) be ) );
	}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add( Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockState newState = getDefaultState();
		newState = newState.getStateForNeighborUpdate( null, newState, itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), itemPlacementContext.getBlockPos() );

    	return newState.with(Properties.HORIZONTAL_FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
    }

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof InserterBlockEntity ) {
				ItemScatterer.spawn(world, pos, (InserterBlockEntity)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean notify) {
		Direction direction = blockState.get(Properties.HORIZONTAL_FACING);
		InserterBlockEntity machineBlockEntity = (InserterBlockEntity) world.getBlockEntity(blockPos);

		BlockPos frontPos = blockPos.offset(direction);
		BlockPos behindPos = blockPos.offset(direction.getOpposite());

		BlockEntity frontBlockEntity = world.getBlockEntity(frontPos);
		if (frontBlockEntity instanceof Inventory && !(frontBlockEntity instanceof InserterBlockEntity))
			machineBlockEntity.setHasOutput(true);
		else
			machineBlockEntity.setHasOutput(false);

		BlockEntity behindBlockEntity = world.getBlockEntity(behindPos);
		if (behindBlockEntity instanceof Inventory && !(frontBlockEntity instanceof InserterBlockEntity))
			machineBlockEntity.setHasInput(true);
		else
			machineBlockEntity.setHasInput(false);
	}

	@Override
	public BlockRenderType getRenderType( BlockState state )
	{
		return BlockRenderType.MODEL;
	}
}