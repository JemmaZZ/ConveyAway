package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.api.Conveyor;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FunnelBlockEntity extends ConveyorBlockEntity
{
    public FunnelBlockEntity( BlockPos blockPos, BlockState blockState )
    {
        super( ConveyAwayBlockEntities.FUNNEL, blockPos, blockState );
    }

    public static void tick( World world, BlockPos pos, BlockState state, FunnelBlockEntity be )
    {
        be.tick( world, pos, state );
    }

    @Override
    public void tick( World world, BlockPos pos, BlockState state )
    {
        if( !isEmpty() && world.getBlockEntity( pos.offset( Direction.DOWN ) ) instanceof ConveyorBlockEntity nextFunnel )
        {
            if( nextFunnel.isEmpty() || nextFunnel.position > 0 )
            {
                if( position < getSpeed() )
                {
                    prevPosition = position;
                    position += 1;
                }
                else
                {
                    prevPosition = -1;
                    position = 0;
                    nextFunnel.give( getStack() );
                    clear();
                    if( !world.isClient() )
                    {
                        sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
                    }
                }
            }
            else
            {
                prevPosition = 0;
                position = 0;
            }
        } else if( !isEmpty() && world.isAir( pos.offset( Direction.DOWN ) ) ) {
            if( position < getSpeed() )
            {
                prevPosition = position;
                position += 1;
            }
            else
            {
                prevPosition = -1;
                position = 0;
                ItemEntity itemEntity = new ItemEntity( world, getPos().getX() + 0.5, getPos().getY() - 0.5, getPos().getZ() + 0.5, getStack() );
                itemEntity.setVelocity( 0,  0, 0 );
                if ( !world.isClient() )
                    world.spawnEntity( itemEntity );
                clear();
                if( !world.isClient() )
                {
                    sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
                }
            }
        }
        else
        {
            position = 0;
            prevPosition = 0;
        }
    }

    @Override
    public void give( ItemStack stack )
    {
        setStack( stack );
        prevPosition = -1;
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
    }

    @Override
    public void readNbt( NbtCompound nbt )
    {
        super.readNbt( nbt );
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
