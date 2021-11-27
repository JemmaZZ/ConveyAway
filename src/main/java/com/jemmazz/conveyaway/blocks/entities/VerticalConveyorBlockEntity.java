package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
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

public class VerticalConveyorBlockEntity extends ConveyorBlockEntity
{
    protected double verticalPosition = 0;
    protected double prevVerticalPosition = 0;

    public VerticalConveyorBlockEntity( BlockPos blockPos, BlockState blockState )
    {
        super( ConveyAwayBlockEntities.VERTICAL_CONVEYOR, blockPos, blockState );
    }

    public static void tick( World world, BlockPos pos, BlockState state, VerticalConveyorBlockEntity be )
    {
        be.tick( world, pos, state );
    }

    @Override
    public void tick( World world, BlockPos pos, BlockState state )
    {
        Direction facing = getCachedState().get( Properties.HORIZONTAL_FACING );

        if( !isEmpty() && getCachedState().get( Conveyor.TOP ) && world.getBlockEntity( pos.offset( Direction.UP ) ) instanceof VerticalConveyorBlockEntity nextConveyor )
        {
            if( nextConveyor.isEmpty() || nextConveyor.verticalPosition > 0 )
            {
                if( !getCachedState().get( Conveyor.BACK ) && verticalPosition < getSpeed() || getCachedState().get( Conveyor.BACK ) && verticalPosition < (getSpeed() - getSpeed() / 4) )
                {
                    prevVerticalPosition = verticalPosition;
                    verticalPosition += 1;
                    prevPosition = 0;
                }
                else
                {
                    prevVerticalPosition = -1;
                    verticalPosition = 0;
                    nextConveyor.give( getStack() );
                    clear();
                    if( !world.isClient() )
                    {
                        sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
                    }
                }
            }
            else
            {
                prevVerticalPosition = 0;
                verticalPosition = 0;
                prevPosition = 0;
                position = 0;
            }
        }
        else
        if( !isEmpty() && getCachedState().get( Conveyor.FRONT ) && world.getBlockEntity( pos.offset( facing ) ) instanceof ConveyorBlockEntity nextConveyor ) {
            if( nextConveyor.isEmpty() )
            {
                if( verticalPosition < getSpeed() / 4 )
                {
                    prevVerticalPosition = verticalPosition;
                    if (verticalPosition + 1 > getSpeed() / 4)
                    {
                        verticalPosition = getSpeed() / 4;
                    } else
                        verticalPosition += 1;
                    prevPosition = 0;
                } else if ( nextConveyor.getConveyor().isFlat() && position < getSpeed() + (getSpeed() / 4) || position < getSpeed() )
                {
                    prevVerticalPosition = getSpeed() / 4;
                    prevPosition = position;
                    position += 1;
                }
                else
                {
                    prevPosition = -1;
                    position = 0;
                    nextConveyor.give( getStack() );
                    clear();
                    if( !world.isClient() )
                    {
                        sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
                    }
                }
            }
            else
            {
                prevVerticalPosition = 0;
                verticalPosition = 0;
                prevPosition = 0;
                position = 0;
            }
        }
        else
        {
            prevVerticalPosition = 0;
            verticalPosition = 0;
            position = 0;
            prevPosition = 0;
        }
    }

    public double getVerticalPosition()
    {
        return verticalPosition;
    }

    public void setVerticalPosition( int verticalPosition )
    {
        this.verticalPosition = verticalPosition;
    }

    public double getPrevVerticalPosition()
    {
        return prevVerticalPosition;
    }

    public void setPrevVerticalPosition( int prevVerticalPosition )
    {
        this.prevVerticalPosition = prevVerticalPosition;
    }

    @Override
    public void give( ItemStack stack )
    {
        Direction facing = getCachedState().get( Properties.HORIZONTAL_FACING );

        setStack( stack );
        if( getCachedState().get( Conveyor.TOP ) )
        {
            prevVerticalPosition = -1;
        }
        else
        {
            prevVerticalPosition = 0;
        }
        verticalPosition = 0;
        if ( !getCachedState().get( Conveyor.FRONT ) && world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock() instanceof Conveyor && (((Conveyor) world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock()).isFlat() || !((Conveyor) world.getBlockState( pos.offset( facing.getOpposite() ) ).getBlock()).isFlat()) && world.getBlockState( pos.offset( facing.getOpposite() ) ).get( Conveyor.FRONT ) )
        {
            prevPosition = -1;
            prevVerticalPosition = 0;
        } else
            prevPosition = 0;
        position = 0;
        if( !world.isClient() )
        {
            sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
        }
    }

    @Override
    protected void sendPacket( ServerWorld w, NbtCompound tag )
    {
        tag.putString( "id", BlockEntityType.getId( getType() ).toString() );
        sendPacket( w, BlockEntityUpdateS2CPacket.create( this, blockEntity -> tag ) );
    }

    @Override
    protected void sendPacket( ServerWorld world, BlockEntityUpdateS2CPacket packet )
    {
        world.getPlayers( player -> player.squaredDistanceTo( Vec3d.of( getPos() ) ) < 40 * 40 ).forEach( player -> player.networkHandler.sendPacket( packet ) );
    }

    @Override
    public void writeNbt( NbtCompound nbt )
    {
        super.writeNbt( nbt );
        nbt.putDouble( "verticalPosition", verticalPosition );
        nbt.putDouble( "prevVerticalPosition", prevVerticalPosition );
    }

    @Override
    public void readNbt( NbtCompound nbt )
    {
        super.readNbt( nbt );
        verticalPosition = nbt.getInt( "verticalPosition" );
        prevVerticalPosition = nbt.getInt( "prevVerticalPosition" );
    }

    @Override
    public NbtCompound writeToNbt( NbtCompound nbt )
    {
        writeNbt( nbt );
        return nbt;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return writeToNbt( new NbtCompound() );
    }
}
