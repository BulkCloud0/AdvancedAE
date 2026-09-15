package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

/**
 * Minimal 1.16.5-compatible replacement for the AE2AddonLib item definition.
 * Keeps AdvancedAE's registration call sites stable while the original library
 * has no Minecraft 1.16.5 release.
 */
public class LibItemDefinition<T extends Item> implements IItemProvider, Supplier<T> {

    private final String englishName;
    private final RegistryObject<T> item;

    public LibItemDefinition(String englishName, RegistryObject<T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    public String getEnglishName() {
        return englishName;
    }

    public ResourceLocation id() {
        return item.getId();
    }

    public ItemStack stack() {
        return stack(1);
    }

    public ItemStack stack(int stackSize) {
        return new ItemStack(item.get(), stackSize);
    }

    public boolean isSameAs(ItemStack comparableStack) {
        return comparableStack != null && !comparableStack.isEmpty() && comparableStack.getItem() == item.get();
    }

    @Override
    public T get() {
        return item.get();
    }

    @Override
    public T asItem() {
        return item.get();
    }
}
