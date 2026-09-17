package appeng.api.stacks;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

/**
 * Compatibility representation of the type-only AE key API used by newer
 * AdvancedAE sources. AE2 8 stores amount on IAEStack, so this class keeps only
 * identity and lets GenericStack carry the amount separately.
 */
public abstract class AEKey {

    private static final int TYPE_NULL = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FLUID = 2;

    public abstract ITextComponent getDisplayName();

    public abstract CompoundNBT toTagGeneric();

    static void writeNonNullKey(PacketBuffer buffer, AEKey key) {
        if (key instanceof AEItemKey) {
            buffer.writeByte(TYPE_ITEM);
            buffer.writeItemStack(((AEItemKey) key).toStack(), true);
        } else if (key instanceof AEFluidKey) {
            buffer.writeByte(TYPE_FLUID);
            CompoundNBT fluidTag = new CompoundNBT();
            ((AEFluidKey) key).toStack(1).writeToNBT(fluidTag);
            buffer.writeCompoundTag(fluidTag);
        } else {
            throw new IllegalArgumentException("Unsupported AE key type: " + key.getClass().getName());
        }
    }

    public static void writeKey(PacketBuffer buffer, @Nullable AEKey key) {
        if (key == null) {
            buffer.writeByte(TYPE_NULL);
            return;
        }
        writeNonNullKey(buffer, key);
    }

    @Nullable
    public static AEKey readKey(PacketBuffer buffer) {
        int type = buffer.readUnsignedByte();
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_ITEM:
                return AEItemKey.of(buffer.readItemStack());
            case TYPE_FLUID:
                CompoundNBT fluidTag = buffer.readCompoundTag();
                return fluidTag == null ? null : AEFluidKey.of(FluidStack.loadFluidStackFromNBT(fluidTag));
            default:
                throw new IllegalArgumentException("Unknown AE key packet type: " + type);
        }
    }

    @Nullable
    public static AEKey fromTagGeneric(@Nullable CompoundNBT tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        String type = tag.getString("type");
        if ("item".equals(type)) {
            return AEItemKey.of(ItemStack.read(tag.getCompound("stack")));
        }
        if ("fluid".equals(type)) {
            return AEFluidKey.of(FluidStack.loadFluidStackFromNBT(tag.getCompound("stack")));
        }
        return null;
    }
}
