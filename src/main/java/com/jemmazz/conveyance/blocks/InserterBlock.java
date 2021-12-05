package com.jemmazz.conveyance.blocks;

import com.jemmazz.conveyance.blocks.entities.InserterBlockEntity;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InserterBlock extends BlockWithEntity
{
    private String type;
    private int speed;

    public InserterBlock( String type, int speed, Settings settings )
    {
        super( settings );

        this.type = type;
        this.speed = speed;
    }

    public String getType()
    {
        return type;
    }

    public int getSpeed()
    {
        return speed;
    }

    @Override
    public BlockEntity createBlockEntity( BlockPos pos, BlockState state )
    {
        return new InserterBlockEntity( pos, state );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( World world, BlockState state, BlockEntityType<T> type )
    {
        return checkType( type, ConveyanceBlockEntities.INSERTER, ( world1, pos, state1, be ) -> InserterBlockEntity.tick( world1, pos, state1, (InserterBlockEntity) be ) );
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> stateManagerBuilder )
    {
        stateManagerBuilder.add( Properties.HORIZONTAL_FACING );
    }

    @Override
    public BlockState getPlacementState( ItemPlacementContext itemPlacementContext )
    {
        BlockState newState = getDefaultState();
        newState = newState.getStateForNeighborUpdate( null, newState, itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), itemPlacementContext.getBlockPos() );

        return newState.with( Properties.HORIZONTAL_FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing() );
    }

    @Override
    public void onStateReplaced( BlockState state, World world, BlockPos pos, BlockState newState, boolean moved )
    {
        if( !state.isOf( newState.getBlock() ) )
        {
            BlockEntity blockEntity = world.getBlockEntity( pos );
            if( blockEntity instanceof InserterBlockEntity )
            {
                ItemScatterer.spawn( world, pos, (InserterBlockEntity) blockEntity );
                world.updateComparators( pos, this );
            }

            super.onStateReplaced( state, world, pos, newState, moved );
        }
    }

    @Override
    public VoxelShape getOutlineShape( BlockState state, BlockView world, BlockPos pos, ShapeContext context )
    {
        return VoxelShapes.cuboid( 0, 0, 0, 1, 0.5, 1 );
    }

    @Override
    public BlockRenderType getRenderType( BlockState state )
    {
        return BlockRenderType.MODEL;
    }
}