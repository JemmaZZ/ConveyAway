package com.jemmazz.conveyance.blocks;

import com.jemmazz.conveyance.api.Conveyor;
import com.jemmazz.conveyance.blocks.entities.VerticalConveyorBlockEntity;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import com.jemmazz.conveyance.utilities.MovementUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VerticalConveyorBlock extends ConveyorBlock
{
    public VerticalConveyorBlock( Settings settings, double speed )
    {
        super( settings, speed );

        setDefaultState( getDefaultState().with( Conveyor.TOP, false ) );
    }

    @Override
    public boolean isFlat()
    {
        return false;
    }

    @Override
    public Identifier getId()
    {
        Identifier id = super.getId();
        return new Identifier( id.getNamespace(), id.getPath().split( "_", 2 )[1] );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity( BlockPos pos, BlockState state )
    {
        return new VerticalConveyorBlockEntity( pos, state );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( World world, BlockState state, BlockEntityType<T> type )
    {
        return checkType( type, ConveyanceBlockEntities.VERTICAL_CONVEYOR, ( world1, pos, state1, be ) -> VerticalConveyorBlockEntity.tick( world1, pos, state1, (VerticalConveyorBlockEntity) be ) );
    }

    @Override
    public ActionResult onUse( BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit )
    {
        Direction facing = state.get( Properties.HORIZONTAL_FACING );
        if( !player.getStackInHand( hand ).isEmpty() && Block.getBlockFromItem( player.getStackInHand( hand ).getItem() ) instanceof Conveyor )
        {
            Block block = Block.getBlockFromItem( player.getStackInHand( hand ).getItem() );
            Conveyor conveyor = (Conveyor) block;

            if( ( hit.getSide() == Direction.UP || hit.getSide() == facing.getOpposite() ) && conveyor.isFlat() && world.getBlockState( pos.offset( Direction.DOWN ) ).getBlock() instanceof Conveyor || !state.get( Conveyor.BACK ) && hit.getSide() == facing.getOpposite() && !conveyor.isFlat() )
            {
                if( world.isAir( pos.offset( facing ) ) )
                {
                    if( !conveyor.isFlat() )
                    {
                        world.setBlockState( pos.offset( facing ), block.getDefaultState().with( Properties.HORIZONTAL_FACING, facing ).with( Conveyor.BACK, true ), Block.NOTIFY_ALL );
                    }
                    else
                    {
                        world.setBlockState( pos.offset( facing ), block.getDefaultState().with( Properties.HORIZONTAL_FACING, facing ), Block.NOTIFY_ALL );
                    }
                }
                else
                {
                    return super.onUse( state, world, pos, player, hand, hit );
                }

                if( !player.isCreative() )
                {
                    player.getStackInHand( hand ).decrement( 1 );
                }

                return ActionResult.SUCCESS;
            }
            else
            {
                return super.onUse( state, world, pos, player, hand, hit );
            }
        }
        else
        {
            return super.onUse( state, world, pos, player, hand, hit );
        }
    }

    @Override
    public void onEntityCollision( BlockState blockState, World world, BlockPos blockPos, Entity entity )
    {

    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> stateManagerBuilder )
    {
        stateManagerBuilder.add( Properties.HORIZONTAL_FACING, Conveyor.BACK, Conveyor.TOP, Conveyor.FRONT );
    }

    @Override
    public BlockState getStateForNeighborUpdate( BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos )
    {
        BlockState newState = state;
        Direction facing = state.get( Properties.HORIZONTAL_FACING );

        if( world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock() instanceof Conveyor && facing == world.getBlockState( pos.offset( facing.getOpposite() ) ).get( Properties.HORIZONTAL_FACING ) && (((Conveyor) world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock()).isFlat() || !((Conveyor) world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock()).isFlat()) && world.getBlockState( pos.offset( facing.getOpposite() ) ).get( Conveyor.FRONT ) )
        {
            newState = newState.with( Conveyor.BACK, true );
        }
        else
        {
            newState = newState.with( Conveyor.BACK, false );
        }

        if( !newState.get( Conveyor.BACK ) && world.getBlockState( pos.offset( facing ) ).getBlock() instanceof Conveyor && ( !world.getBlockState( pos.offset( facing ) ).contains( Properties.HORIZONTAL_FACING ) || world.getBlockState( pos.offset( facing ) ).get( Properties.HORIZONTAL_FACING ) == facing ) && world.getBlockState( pos.offset( Direction.DOWN ) ).getBlock() instanceof Conveyor )
        {
            if( !((Conveyor) world.getBlockState( pos.offset( Direction.DOWN ) ).getBlock()).isFlat() && world.getBlockState( pos.offset( Direction.DOWN ) ).get( Conveyor.FRONT ) )
            {
                newState = newState.with( Conveyor.FRONT, false );
            }
            else
            {
                newState = newState.with( Conveyor.FRONT, true );
            }
        }
        else
        {
            newState = newState.with( Conveyor.FRONT, false );
        }

        if( !newState.get( Conveyor.FRONT ) && world.getBlockState( pos.offset( Direction.UP ) ).getBlock() instanceof Conveyor && facing == world.getBlockState( pos.offset( Direction.UP ) ).get( Properties.HORIZONTAL_FACING ) && !((Conveyor) world.getBlockState( pos.offset( Direction.UP ) ).getBlock()).isFlat() )
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
        VoxelShape conveyor = VoxelShapes.cuboid( 0, 0, 0.5, 1, 1, 1 );
        VoxelShape conveyorConnect = VoxelShapes.cuboid( 0, 0, 0, 1, 0.5, 1 );

        if( blockState.get( Conveyor.FRONT ) )
        {
            conveyor = VoxelShapes.cuboid( 0, 0, 0.5, 1, 0.5, 1 );
        }
        return blockState.get( Conveyor.BACK ) ? MovementUtilities.rotateShape( Direction.NORTH, blockState.get( Properties.HORIZONTAL_FACING ).getOpposite(), VoxelShapes.union( conveyor, conveyorConnect ) ) : MovementUtilities.rotateShape( Direction.NORTH, blockState.get( Properties.HORIZONTAL_FACING ).getOpposite(), conveyor );
    }
}
