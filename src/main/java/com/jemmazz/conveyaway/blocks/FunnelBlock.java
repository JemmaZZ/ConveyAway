package com.jemmazz.conveyaway.blocks;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.blocks.entities.FunnelBlockEntity;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
        return checkType( type, ConveyAwayBlockEntities.FUNNEL, ( world1, pos, state1, be ) -> FunnelBlockEntity.tick( world1, pos, state1, (FunnelBlockEntity) be ) );
    }

    @Override
    public ActionResult onUse( BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit )
    {
        FunnelBlockEntity blockEntity = (FunnelBlockEntity) world.getBlockEntity( pos );

        if( !player.getStackInHand( hand ).isEmpty() && Block.getBlockFromItem( player.getStackInHand( hand ).getItem() ) instanceof FunnelBlock )
            return ActionResult.PASS;
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
