package appeng.api.stacks;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

/** Java 8 / AE2 8 compatibility wrapper for the newer type-only fluid key API. */
public final class AEFluidKey extends AEKey {

    private final FluidStack stack;

    private AEFluidKey(FluidStack stack) {
        this.stack = stack.copy();
        this.stack.setAmount(1);
    }

    @Nullable
    public static AEFluidKey of(@Nullable FluidStack stack) {
        return stack == null || stack.isEmpty() ? null : new AEFluidKey(stack);
    }

    @Nullable
    public static AEFluidKey of(@Nullable Fluid fluid) {
        return fluid == null ? null : of(new FluidStack(fluid, 1));
    }

    public Fluid getFluid() {
        return this.stack.getFluid();
    }

    public FluidStack toStack() {
        return this.toStack(1);
    }

    public FluidStack toStack(int amount) {
        FluidStack result = this.stack.copy();
        result.setAmount(amount);
        return result;
    }

    public boolean matches(FluidStack other) {
        return other != null && !other.isEmpty() && this.stack.isFluidEqual(other)
                && Objects.equals(this.stack.getTag(), other.getTag());
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
        result.putString("type", "fluid");
        CompoundNBT stackTag = new CompoundNBT();
        this.stack.writeToNBT(stackTag);
        result.put("stack", stackTag);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AEFluidKey)) {
            return false;
        }
        AEFluidKey other = (AEFluidKey) obj;
        return this.stack.isFluidEqual(other.stack) && Objects.equals(this.stack.getTag(), other.stack.getTag());
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.stack.getFluid()) + Objects.hashCode(this.stack.getTag());
    }

    @Override
    public String toString() {
        return "AEFluidKey[" + this.stack + "]";
    }
}
