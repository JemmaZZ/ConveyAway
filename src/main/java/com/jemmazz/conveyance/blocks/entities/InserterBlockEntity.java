package com.jemmazz.conveyance.blocks.entities;

import com.jemmazz.conveyance.blocks.InserterBlock;
import com.jemmazz.conveyance.init.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.stream.IntStream;

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, RenderAttachmentBlockEntity
{
    protected int position = 0;
    protected int prevPosition = 0;
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize( 1, ItemStack.EMPTY );

    public InserterBlockEntity( BlockEntityType type, BlockPos blockPos, BlockState blockState )
    {
        super( type, blockPos, blockState );
    }

    public InserterBlockEntity( BlockPos pos, BlockState state )
    {
        this( ConveyanceBlockEntities.INSERTER, pos, state );
    }

    public static void tick( World world, BlockPos pos, BlockState state, InserterBlockEntity be )
    {
        be.tick( world, pos, state );
    }

    private static IntStream getAvailableSlots( Inventory inventory, Direction side )
    {
        return inventory instanceof SidedInventory ? IntStream.of( ((SidedInventory) inventory).getAvailableSlots( side ) ) : IntStream.range( 0, inventory.size() );
    }

    public static ItemStack transfer( Inventory from, Inventory to, ItemStack stack, Direction side )
    {
        if( to instanceof SidedInventory && side != null )
        {
            SidedInventory sidedInventory = (SidedInventory) to;
            int[] is = sidedInventory.getAvailableSlots( side );

            for( int i = 0; i < is.length && !stack.isEmpty(); ++i )
            {
                stack = transfer( from, to, stack, is[i], side );
            }
        }
        else
        {
            int j = to.size();

            for( int k = 0; k < j && !stack.isEmpty(); ++k )
            {
                stack = transfer( from, to, stack, k, side );
            }
        }

        return stack;
    }

    private static boolean canInsert( Inventory inventory, ItemStack stack, int slot, Direction side )
    {
        if( !inventory.isValid( slot, stack ) )
        {
            return false;
        }
        else
        {
            return !(inventory instanceof SidedInventory) || ((SidedInventory) inventory).canInsert( slot, stack, side );
        }
    }

    private static boolean canMergeItems( ItemStack first, ItemStack second )
    {
        if( first.getItem() != second.getItem() )
        {
            return false;
        }
        else if( first.getDamage() != second.getDamage() )
        {
            return false;
        }
        else if( first.getCount() > first.getMaxCount() )
        {
            return false;
        }
        else
        {
            return ItemStack.areNbtEqual( first, second );
        }
    }

    private static boolean canExtract( Inventory inv, ItemStack stack, int slot, Direction facing )
    {
        return !(inv instanceof SidedInventory) || ((SidedInventory) inv).canExtract( slot, stack, facing );
    }

    private static boolean extract( SingularStackInventory singularStackInventory, Inventory inventory, int slot, Direction side )
    {
        ItemStack itemStack = inventory.getStack( slot );
        if( !itemStack.isEmpty() && canExtract( inventory, itemStack, slot, side ) )
        {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = transfer( inventory, singularStackInventory, inventory.removeStack( slot, inventory.getStack( slot ).getCount() ), null );
            if( itemStack3.isEmpty() )
            {
                inventory.markDirty();
                return true;
            }

            inventory.setStack( slot, itemStack2 );
        }

        return false;
    }

    private static ItemStack transfer( Inventory from, Inventory to, ItemStack stack, int slot, Direction direction )
    {
        ItemStack itemStack = to.getStack( slot );
        if( canInsert( to, stack, slot, direction ) )
        {
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if( itemStack.isEmpty() )
            {
                to.setStack( slot, stack );
                stack = ItemStack.EMPTY;
                bl = true;
            }
            else if( canMergeItems( itemStack, stack ) )
            {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min( stack.getCount(), i );
                stack.decrement( j );
                itemStack.increment( j );
                bl = j > 0;
            }
        }

        return stack;
    }

    @Override
    public int getMaxCountPerStack()
    {
        return 1;
    }

    public void tick( World world, BlockPos pos, BlockState state )
    {
        Direction direction = state.get( HorizontalFacingBlock.FACING );
        int speed = ((InserterBlock) state.getBlock()).getSpeed();

        if( isEmpty() && !world.isClient() && !(world.getBlockState( pos.offset( direction.getOpposite() ) ).getBlock() instanceof InserterBlock) )
        {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find( world, pos.offset( direction.getOpposite() ), direction );
            if( storage != null && storage.supportsExtraction() )
            {
                if( position == 0 )
                {
                    try( Transaction transaction = Transaction.openOuter() )
                    {
                        ItemVariant toExtract = StorageUtil.findExtractableResource( storage, transaction );
                        if( toExtract != null && storage.extract( toExtract, 1, transaction ) == 1 )
                        {
                            setStack( toExtract.toStack() );
                            transaction.commit();
                        }
                    }
                }
                else if( position > 0 )
                {
                    setPosition( getPosition() - 1 );
                }
            }
        }
        else if( !isEmpty() )
        {
            Inventory inventory = (Inventory) world.getBlockEntity( pos.offset( direction ) );

            if( position < speed )
            {
                setPosition( getPosition() + 1 );
            }
            else if( position == speed && !world.isClient() )
            {
                Storage<ItemVariant> storage = ItemStorage.SIDED.find( world, pos.offset( direction ), direction.getOpposite() );
                if( storage != null && storage.supportsInsertion() )
                {
                    try( Transaction transaction = Transaction.openOuter() )
                    {
                        ItemVariant variant = ItemVariant.of( getStack() );
                        if( storage.insert( variant, 1, transaction ) == 1 )
                        {
                            clear();
                            transaction.commit();
                        }
                    }
                }
                prevPosition = speed;
            }
            else
            {
                prevPosition = speed;
            }
        }
        else if( position > 0 )
        {
            setPosition( getPosition() - 1 );
        }
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

    private boolean isInventoryFull( Inventory inv, Direction direction )
    {
        return getAvailableSlots( inv, direction ).allMatch( i -> {
            ItemStack itemStack = inv.getStack( i );
            return itemStack.getCount() >= itemStack.getMaxCount();
        } );
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return stacks;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public ItemStack removeStack()
    {
        position = 15;
        prevPosition = 15;
        return SingularStackInventory.super.removeStack();
    }

    @Override
    public int[] getRenderAttachmentData()
    {
        return new int[] { position, prevPosition };
    }


    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        if( position == 0 )
        {
            prevPosition = 0;
        }
        else
        {
            prevPosition = this.position;
        }
        this.position = position;
    }

    public int getPrevPosition()
    {
        return prevPosition;
    }

    public void sync()
    {
        if( world instanceof ServerWorld )
        {
            sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
        }
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        sync();
    }

    @Override
    public void readNbt( NbtCompound nbtCompound )
    {
        super.readNbt( nbtCompound );
        clear();
        setStack( ItemStack.fromNbt( nbtCompound.getCompound( "stack" ) ) );
        position = nbtCompound.getInt( "position" );
    }

    @Override
    public void writeNbt( NbtCompound nbtCompound )
    {
        nbtCompound.put( "stack", getStack().writeNbt( new NbtCompound() ) );
        nbtCompound.putInt( "position", position );
    }

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