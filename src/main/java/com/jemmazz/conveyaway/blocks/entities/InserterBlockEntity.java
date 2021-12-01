package com.jemmazz.conveyaway.blocks.entities;

import com.jemmazz.conveyaway.ConveyAway;
import com.jemmazz.conveyaway.blocks.InserterBlock;
import com.jemmazz.conveyaway.init.ConveyAwayBlockEntities;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
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

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, RenderAttachmentBlockEntity {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected int position = 0;
    protected int prevPosition = 0;
    protected boolean hasInput = false;
    protected boolean hasOutput = false;

	public InserterBlockEntity( BlockEntityType type, BlockPos blockPos, BlockState blockState )
	{
		super( type, blockPos, blockState );
	}

    public InserterBlockEntity( BlockPos pos, BlockState state ) {
        this( ConveyAwayBlockEntities.INSERTER, pos, state );
    }

	@Override
	public int getMaxCountPerStack()
	{
		return 1;
	}

	public static void tick( World world, BlockPos pos, BlockState state, InserterBlockEntity be )
	{
		be.tick( world, pos, state );
	}
	
	public void tick( World world, BlockPos pos, BlockState state )
	{
		Direction direction = state.get(HorizontalFacingBlock.FACING);
		int speed = ((InserterBlock) state.getBlock()).getSpeed();

		if (isEmpty() && !world.isClient() && !(world.getBlockState( pos.offset( direction.getOpposite() ) ).getBlock() instanceof InserterBlock) && world.getBlockEntity(pos.offset(direction.getOpposite())) instanceof Inventory) {
			Inventory inventory = (Inventory) world.getBlockEntity(pos.offset(direction.getOpposite()));
			if (position == 0) {
				int slotToUse = -1;
				for (int i = 0; i < inventory.size(); i++) {
					if (!inventory.getStack(i).isEmpty()) {
						slotToUse = i;
						break;
					}
				}

				if (slotToUse != -1)
					if (extract(this, inventory, slotToUse, direction))
						markDirty();
			} else if (position > 0) {
				setPosition(getPosition() - 1);
			}
		} else if (!isEmpty() && world.getBlockEntity(pos.offset(direction)) instanceof Inventory && !this.isInventoryFull((Inventory) world.getBlockEntity(pos.offset(direction)), direction.getOpposite())) {
			Inventory inventory = (Inventory) world.getBlockEntity(pos.offset(direction));

			if (position < speed) {
				setPosition(getPosition() + 1);
			} else if (!world.isClient()) {
				ItemStack itemStack2 = transfer(this, inventory, removeStack(0, getStack().getCount()), direction.getOpposite());
				markDirty();
				if (itemStack2.isEmpty()) {
					inventory.markDirty();
				}
			}
		} else if (position > 0) {
			setPosition(getPosition() - 1);
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

	public boolean hasInput() {
		return hasInput;
	}

	public boolean hasOutput() {
		return hasOutput;
	}

	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}

	public void setHasOutput(boolean hasOutput) {
		this.hasOutput = hasOutput;
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
	}

	private boolean isInventoryFull(Inventory inv, Direction direction) {
		return getAvailableSlots(inv, direction).allMatch((i) -> {
			ItemStack itemStack = inv.getStack(i);
			return itemStack.getCount() >= itemStack.getMaxCount();
		});
	}

	public static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, Direction side) {
		if (to instanceof SidedInventory && side != null) {
			SidedInventory sidedInventory = (SidedInventory)to;
			int[] is = sidedInventory.getAvailableSlots(side);

			for(int i = 0; i < is.length && !stack.isEmpty(); ++i) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.size();

			for(int k = 0; k < j && !stack.isEmpty(); ++k) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
		if (!inventory.isValid(slot, stack)) {
			return false;
		} else {
			return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
		}
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else if (first.getCount() > first.getMaxCount()) {
			return false;
		} else {
			return ItemStack.areNbtEqual(first, second);
		}
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
	}

	private static boolean extract(SingularStackInventory singularStackInventory, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, singularStackInventory, inventory.removeStack(slot, inventory.getStack(slot).getCount()), (Direction)null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setStack(slot, itemStack2);
		}

		return false;
	}

	private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction) {
		ItemStack itemStack = to.getStack(slot);
		if (canInsert(to, stack, slot, direction)) {
			boolean bl = false;
			boolean bl2 = to.isEmpty();
			if (itemStack.isEmpty()) {
				to.setStack(slot, stack);
				stack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack, stack)) {
				int i = stack.getMaxCount() - itemStack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.decrement(j);
				itemStack.increment(j);
				bl = j > 0;
			}
		}

		return stack;
	}

	@Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

	@Override
	public int size() {
		return 1;
	}

	@Override
    public ItemStack removeStack() {
        position = 15;
        prevPosition = 15;
        return SingularStackInventory.super.removeStack();
    }

    @Override
    public int[] getRenderAttachmentData() {
        return new int[] { position, prevPosition };
    }


    public int getPosition() {
        return position;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    public void setPosition(int position) {
        if (position == 0)
            this.prevPosition = 0;
        else
            this.prevPosition = this.position;
        this.position = position;
    }

    public void sync() {
		if (world instanceof ServerWorld) {
			sendPacket( (ServerWorld) world, writeToNbt( new NbtCompound() ) );
		}
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sync();
    }

	@Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);
        clear();
        setStack(ItemStack.fromNbt(nbtCompound.getCompound("stack")));
        position = nbtCompound.getInt("position");
        hasInput = nbtCompound.getBoolean("hasInput");
        hasOutput = nbtCompound.getBoolean("hasOutput");
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound) {
        nbtCompound.put("stack", getStack().writeNbt(new NbtCompound()));
        nbtCompound.putInt("position", position);
        nbtCompound.putBoolean("hasInput", hasInput);
        nbtCompound.putBoolean("hasOutput", hasOutput);
    }

	public NbtCompound writeToNbt( NbtCompound nbt )
	{
		writeNbt( nbt );
		return nbt;
	}

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return writeToNbt(new NbtCompound());
    }
}