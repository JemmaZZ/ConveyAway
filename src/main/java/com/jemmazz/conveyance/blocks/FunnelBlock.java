package com.jemmazz.conveyance.blocks;

import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyance.blocks.entities.FunnelBlockEntity;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FunnelBlock extends BlockWithEntity implements Conveyor
{
    private final double speed;
    private Identifier id;

    public FunnelBlock( Settings settings, double speed )
    {
        super( settings );

        this.speed = speed;
        setDefaultState( getDefaultState().with( Conveyor.TOP, false ) );
    }

    @Override
    public boolean isFlat()
    {
        return false;
    }

    @Override
    public double getSpeed()
    {
        return speed;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public void setId( Identifier id )
    {
        this.id = id;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity( BlockPos pos, BlockState state )
    {
        return new FunnelBlockEntity( pos, state );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( World world, BlockState state, BlockEntityType<T> type )
    {
        return checkType( type, ConveyanceBlockEntities.FUNNEL, ( world1, pos, state1, be ) -> FunnelBlockEntity.tick( world1, pos, state1, (FunnelBlockEntity) be ) );
    }

    @Override
    public void onEntityCollision( BlockState state, World world, BlockPos pos, Entity entity )
    {
        if( entity instanceof ItemEntity && entity.getBlockPos().equals( pos ) && world.getBlockEntity( pos ) instanceof FunnelBlockEntity && ((FunnelBlockEntity) world.getBlockEntity( pos )).isInvEmpty() )
        {
            FunnelBlockEntity blockEntity = (FunnelBlockEntity) world.getBlockEntity( pos );

            if( !world.isClient() && blockEntity.isEmpty() )
            {
                ItemStack copy = ((ItemEntity) entity).getStack().copy();
                copy.setCount( 1 );
                blockEntity.setStack( copy );
                ((ItemEntity) entity).getStack().decrement( 1 );
            }
        }
    }

    @Override
    public void onStateReplaced( BlockState state, World world, BlockPos pos, BlockState newState, boolean moved )
    {
        if( !state.isOf( newState.getBlock() ) )
        {
            BlockEntity blockEntity = world.getBlockEntity( pos );
            if( blockEntity instanceof ConveyorBlockEntity )
            {
                ItemScatterer.spawn( world, pos, (ConveyorBlockEntity) blockEntity );
                world.updateComparators( pos, this );
            }

            super.onStateReplaced( state, world, pos, newState, moved );
        }
    }

    @Override
    public ActionResult onUse( BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit )
    {
        FunnelBlockEntity blockEntity = (FunnelBlockEntity) world.getBlockEntity( pos );

        if( !player.getStackInHand( hand ).isEmpty() && Block.getBlockFromItem( player.getStackInHand( hand ).getItem() ) instanceof FunnelBlock )
        {
            return ActionResult.PASS;
        }
        else if( !player.getStackInHand( hand ).isEmpty() && blockEntity.isEmpty() )
        {
            if( !world.isClient() )
            {
                ItemStack heldStack = player.getStackInHand( hand ).copy();
                heldStack.setCount( 1 );
                blockEntity.give( heldStack );

                player.getStackInHand( hand ).decrement( 1 );
            }

            return ActionResult.SUCCESS;
        }
        else if( !blockEntity.isEmpty() )
        {
            if( !world.isClient() )
            {
                player.getInventory().offerOrDrop( blockEntity.getStack() );

                blockEntity.give( ItemStack.EMPTY );
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> stateManagerBuilder )
    {
        stateManagerBuilder.add( Conveyor.TOP );
    }

    @Override
    public BlockState getStateForNeighborUpdate( BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos )
    {
        BlockState newState = state;

        if( world.getBlockState( pos.offset( Direction.UP ) ).getBlock() instanceof FunnelBlock )
        {
            newState = newState.with( Conveyor.TOP, true );
        }
        else
        {
            newState = newState.with( Conveyor.TOP, false );
        }
        return newState;
    }

    @Override
    public BlockState getPlacementState( ItemPlacementContext itemPlacementContext )
    {
        World world = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        BlockState newState = getDefaultState();

        newState = newState.getStateForNeighborUpdate( null, newState, world, blockPos, blockPos );

        return newState;
    }

    @Override
    public boolean isTranslucent( BlockState blockState, BlockView blockView, BlockPos blockPos )
    {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape( BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext entityContext )
    {
        VoxelShape shape = VoxelShapes.cuboid( 0, 0, 0, 1, 0.5, 1 );
        return blockState.get( Conveyor.TOP ) ? VoxelShapes.fullCube() : shape;
    }

    @Override
    public BlockRenderType getRenderType( BlockState state )
    {
        return BlockRenderType.MODEL;
    }
}
