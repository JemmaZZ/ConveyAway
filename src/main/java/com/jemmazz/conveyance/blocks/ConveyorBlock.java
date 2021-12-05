package com.jemmazz.conveyance.blocks;

import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.entities.ConveyorBlockEntity;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import com.jemmazz.conveyance.utilities.MovementUtilities;
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
import net.minecraft.state.property.Properties;
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

public class ConveyorBlock extends BlockWithEntity implements Conveyor
{
    private final double speed;
    private Identifier id;

    public ConveyorBlock( Settings settings, double speed )
    {
        super( settings );

        this.speed = speed;
        setDefaultState( getDefaultState().with( Conveyor.FRONT, false ).with( Conveyor.BACK, false ) );
    }

    @Override
    public boolean isFlat()
    {
        return true;
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
        return new ConveyorBlockEntity( pos, state );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( World world, BlockState state, BlockEntityType<T> type )
    {
        return checkType( type, ConveyanceBlockEntities.CONVEYOR, ( world1, pos, state1, be ) -> ConveyorBlockEntity.tick( world1, pos, state1, (ConveyorBlockEntity) be ) );
    }

    @Override
    public boolean hasComparatorOutput( BlockState blockState )
    {
        return true;
    }

    @Override
    public int getComparatorOutput( BlockState blockState, World world, BlockPos blockPos )
    {
        //        return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
        return 0;
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
        Direction facing = state.get( Properties.HORIZONTAL_FACING );
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity( pos );

        if( !player.getStackInHand( hand ).isEmpty() && Block.getBlockFromItem( player.getStackInHand( hand ).getItem() ) instanceof Conveyor && !(Block.getBlockFromItem( player.getStackInHand( hand ).getItem() ) instanceof FunnelBlock) )
        {
            Block block = Block.getBlockFromItem( player.getStackInHand( hand ).getItem() );
            Conveyor conveyor = (Conveyor) block;

            if( isFlat() && hit.getSide() == Direction.UP && !conveyor.isFlat() )
            {
                world.setBlockState( pos.offset( facing ), block.getDefaultState().with( Properties.HORIZONTAL_FACING, facing ), Block.NOTIFY_ALL );

                if( !player.isCreative() )
                {
                    player.getStackInHand( hand ).decrement( 1 );
                }

                return ActionResult.SUCCESS;
            }
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
    public void onEntityCollision( BlockState blockState, World world, BlockPos blockPos, Entity entity )
    {
        BlockPos pos = new BlockPos( entity.getPos() );

        if( !entity.isOnGround() || (entity.getY() - blockPos.getY()) != (8F / 16F) )
        {
            return;
        }

        if( entity instanceof PlayerEntity && entity.isSneaking() )
        {
            return;
        }

        Direction direction = blockState.get( Properties.HORIZONTAL_FACING );

        if( entity instanceof ItemEntity && pos.equals( blockPos ) && world.getBlockEntity( blockPos ) instanceof ConveyorBlockEntity )
        {
            ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity( blockPos );

            //            if (blockEntity.isEmpty()) {
            //                blockEntity.setStack(((ItemEntity) entity).getStack());
            //                entity.remove();
            //            }
        }
        else if( !(entity instanceof ItemEntity) )
        {
            MovementUtilities.pushEntity( entity, blockPos, (float) (2.0F / speed), direction );
        }
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> stateManagerBuilder )
    {
        stateManagerBuilder.add( Properties.HORIZONTAL_FACING, Conveyor.BACK, Conveyor.FRONT );
    }

    @Override
    public BlockState getStateForNeighborUpdate( BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos )
    {
        BlockState newState = state;
        Direction facing = state.get( Properties.HORIZONTAL_FACING );

        if( world.getBlockState( pos.offset( facing ) ).getBlock() instanceof Conveyor || world.getBlockState( pos.offset( facing ) ).getBlock() instanceof InserterBlock )
        {
            newState = newState.with( Conveyor.FRONT, true );
        }
        else
        {
            newState = newState.with( Conveyor.FRONT, false );
        }

        if( world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock() instanceof Conveyor )
        {
            newState = newState.with( Conveyor.BACK, true );
        }
        else
        {
            newState = newState.with( Conveyor.BACK, false );
        }
        return newState;
    }

    @Override
    public BlockState getPlacementState( ItemPlacementContext itemPlacementContext )
    {
        World world = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        BlockState newState = getDefaultState().with( Properties.HORIZONTAL_FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing() );

        newState = newState.getStateForNeighborUpdate( null, newState, world, blockPos, blockPos );

        return newState;
    }

    @Override
    public boolean isTranslucent( BlockState blockState, BlockView blockView, BlockPos blockPos )
    {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape( BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext entityContext )
    {
        VoxelShape conveyor = VoxelShapes.cuboid( 0, 0, 0, 1, 0.5, 1 );
        return conveyor;
    }

    @Override
    public BlockRenderType getRenderType( BlockState state )
    {
        return BlockRenderType.MODEL;
    }
}
