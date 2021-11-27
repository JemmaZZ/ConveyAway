package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConveyorBlockEntity extends BlockEntity implements SingularStackInventory
{
    private final Conveyor conveyor;
    private final DefaultedList<ItemStack> stacks;
    protected double position = 0;
    protected double prevPosition = 0;

    public ConveyorBlockEntity( BlockEntityType type, BlockPos blockPos, BlockState blockState )
    {
        super( type, blockPos, blockState );

        conveyor = (Conveyor) blockState.getBlock();
        stacks = DefaultedList.ofSize( 1, ItemStack.EMPTY );
    }

    public ConveyorBlockEntity( BlockPos blockPos, BlockState blockState )
    {
        this( ConveyAwayBlockEntities.CONVEYOR, blockPos, blockState );
    }

    public static void tick( World world, BlockPos pos, BlockState state, ConveyorBlockEntity be )
    {
        be.tick( world, pos, state );
    }

    public void tick( World world, BlockPos pos, BlockState state )
    {
        Direction facing = getCachedState().get( Properties.HORIZONTAL_FACING );

        if( !isEmpty() && getCachedState().get( Conveyor.FRONT ) && world.getBlockEntity( pos.offset( facing ) ) instanceof ConveyorBlockEntity nextConveyor )
        {
            if( nextConveyor.isEmpty() || nextConveyor.position > 0 )
            {
                if( nextConveyor.getConveyor().isFlat() && position < getSpeed() || !nextConveyor.getConveyor().isFlat() && position < (getSpeed() - getSpeed() / 4) )
                {
                    prevPosition = position;
                    position += 1;
                }
                else
                {
                    prevPosition = -1;
                    position = 0;
                    if( !nextConveyor.getConveyor().isFlat() )
                    {
                        nextConveyor.prevPosition = -1;
                    }
                    nextConveyor.give( getStack() );
                    clear();
                    if( !world.isClient() )
                    {
                        sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
                    }
                    //nextConveyor.setPrevPosition(-1);
                }
            }
            else
            {
                prevPosition = 0;
                position = 0;
            }
        }
        else
        {
            position = 0;
            prevPosition = 0;
        }
    }

    public void give( ItemStack stack )
    {
        prevPosition = -1;
        position = 0;
        setStack( stack );
        if( !world.isClient() )
        {
            sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return stacks;
    }

    public Conveyor getConveyor()
    {
        return conveyor;
    }

    public double getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public double getPrevPosition()
    {
        return prevPosition;
    }

    public void setPrevPosition( int prevPosition )
    {
        this.prevPosition = prevPosition;
    }

    public double getSpeed()
    {
        return conveyor.getSpeed();
    }

    public Identifier getId()
    {
        return conveyor.getId();
    }

    protected void sendPacket( ServerWorld w, NbtCompound tag )
    {
        tag.putString( "id", BlockEntityType.getId( getType() ).toString() );
        tag.putBoolean( "clear", isEmpty() );
        sendPacket( w, BlockEntityUpdateS2CPacket.create( this, blockEntity -> tag ) );
    }

    protected void sendPacket( ServerWorld world, BlockEntityUpdateS2CPacket packet )
    {
        world.getPlayers( player -> player.squaredDistanceTo( Vec3d.of( getPos() ) ) < 40 * 40 ).forEach( player -> player.networkHandler.sendPacket( packet ) );
    }

    @Override
    public void writeNbt( NbtCompound nbt )
    {
        super.writeNbt( nbt );
        NbtCompound stack = new NbtCompound();
        getStack().writeNbt( stack );
        nbt.put( "stack", stack );
        nbt.putDouble( "position", position );
        nbt.putDouble( "prevPosition", prevPosition );
    }

    public NbtCompound writeToNbt( NbtCompound nbt )
    {
        writeNbt( nbt );
        return nbt;
    }

    @Override
    public void readNbt( NbtCompound nbt )
    {
        super.readNbt( nbt );
        setStack( ItemStack.fromNbt( nbt.getCompound( "stack" ) ) );
        position = nbt.getInt( "position" );
        prevPosition = nbt.getInt( "prevPosition" );
        if( nbt.contains( "clear" ) && nbt.getBoolean( "clear" ) )
        {
            clear();
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return writeToNbt( new NbtCompound() );
    }
}
