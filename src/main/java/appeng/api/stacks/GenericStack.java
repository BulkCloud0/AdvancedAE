package appeng.api.stacks;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

/**
 * Compatibility value object for the newer AE2 GenericStack API.
 * Keeps amount separate from the type-only AEKey, matching the modern contract.
 */
public final class GenericStack {

    private final AEKey what;
    private final long amount;

    public GenericStack(AEKey what, long amount) {
        if (what == null) {
            throw new IllegalArgumentException("what cannot be null");
        }
        this.what = what;
        this.amount = amount;
    }

    public AEKey what() {
        return this.what;
    }

    public long amount() {
        return this.amount;
    }

    @Nullable
    public static GenericStack fromItemStack(@Nullable ItemStack stack) {
        AEItemKey key = AEItemKey.of(stack);
        return key == null ? null : new GenericStack(key, stack.getCount());
    }

    public static void writeBuffer(@Nullable GenericStack stack, PacketBuffer buffer) {
        buffer.writeBoolean(stack != null);
        if (stack != null) {
            AEKey.writeKey(buffer, stack.what);
            buffer.writeLong(stack.amount);
        }
    }

    @Nullable
    public static GenericStack readBuffer(PacketBuffer buffer) {
        if (!buffer.readBoolean()) {
            return null;
        }
        AEKey key = AEKey.readKey(buffer);
        long amount = buffer.readLong();
        return key == null ? null : new GenericStack(key, amount);
    }

    public CompoundNBT toTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("what", this.what.toTagGeneric());
        tag.putLong("amount", this.amount);
        return tag;
    }

    @Nullable
    public static GenericStack fromTag(@Nullable CompoundNBT tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        AEKey key = AEKey.fromTagGeneric(tag.getCompound("what"));
        return key == null ? null : new GenericStack(key, tag.getLong("amount"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenericStack)) {
            return false;
        }
        GenericStack other = (GenericStack) obj;
        return this.amount == other.amount && this.what.equals(other.what);
    }

    @Override
    public int hashCode() {
        int result = this.what.hashCode();
        result = 31 * result + (int) (this.amount ^ (this.amount >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GenericStack[" + this.what + " x " + this.amount + "]";
    }
}
