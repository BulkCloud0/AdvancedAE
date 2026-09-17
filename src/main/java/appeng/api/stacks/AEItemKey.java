package appeng.api.stacks;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;

/** Java 8 / AE2 8 compatibility wrapper for the newer type-only item key API. */
public final class AEItemKey extends AEKey {

    private final ItemStack stack;

    private AEItemKey(ItemStack stack) {
        this.stack = stack.copy();
        this.stack.setCount(1);
    }

    @Nullable
    public static AEItemKey of(@Nullable ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : new AEItemKey(stack);
    }

    @Nullable
    public static AEItemKey of(@Nullable IItemProvider item) {
        return item == null ? null : of(new ItemStack(item));
    }

    public Item getItem() {
        return this.stack.getItem();
    }

    public ItemStack toStack() {
        return this.toStack(1);
    }

    public ItemStack toStack(int amount) {
        ItemStack result = this.stack.copy();
        result.setCount(amount);
        return result;
    }

    public boolean matches(ItemStack other) {
        return other != null && !other.isEmpty()
                && ItemStack.areItemsEqual(this.stack, other)
                && ItemStack.areItemStackTagsEqual(this.stack, other);
    }

    public boolean matches(AEKey other) {
        return this.equals(other);
    }

    public boolean matches(GenericStack other) {
        return other != null && this.equals(other.what());
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.stack.getDisplayName();
    }

    @Override
    public CompoundNBT toTagGeneric() {
        CompoundNBT result = new CompoundNBT();
        result.putString("type", "item");
        CompoundNBT stackTag = new CompoundNBT();
        this.stack.write(stackTag);
        result.put("stack", stackTag);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AEItemKey)) {
            return false;
        }
        AEItemKey other = (AEItemKey) obj;
        return ItemStack.areItemsEqual(this.stack, other.stack)
                && ItemStack.areItemStackTagsEqual(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.stack.getItem()) + Objects.hashCode(this.stack.getTag());
    }

    @Override
    public String toString() {
        return "AEItemKey[" + this.stack + "]";
    }
}
