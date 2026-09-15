package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * Minimal 1.16.5 compatibility version of AE2AddonLib's item definition.
 *
 * <p>The original helper library has no 1.16.5 release. Keeping this small
 * wrapper in-tree lets AdvancedAE retain its declarative registry structure
 * while the implementation uses Forge 1.16.5 registries.</p>
 */
public class LibItemDefinition<T extends Item> implements IItemProvider, Supplier<T> {
    private final String englishName;
    private final RegistryObject<T> item;

    public LibItemDefinition(String englishName, RegistryObject<T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public ResourceLocation id() {
        return this.item.getId();
    }

    public ItemStack stack() {
        return stack(1);
    }

    public ItemStack stack(int stackSize) {
        return new ItemStack(this.item.get(), stackSize);
    }

    public boolean is(ItemStack comparableStack) {
        return !comparableStack.isEmpty() && comparableStack.getItem() == this.item.get();
    }

    public boolean isSameAs(ItemStack comparableStack) {
        return is(comparableStack);
    }

    @Override
    public T get() {
        return this.item.get();
    }

    @Override
    public T asItem() {
        return this.item.get();
    }
}
