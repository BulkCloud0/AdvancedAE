package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import appeng.api.definitions.IItemDefinition;
import appeng.api.features.AEFeature;

/**
 * 1.16.5-compatible item definition used by the AdvancedAE backport.
 * It intentionally mirrors the small source API exposed by AE2AddonLib while
 * implementing AE2 8.x's non-generic IItemDefinition contract.
 */
public class LibItemDefinition<T extends Item> implements IItemDefinition, Supplier<T> {
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

    public ResourceLocation getId() {
        return id();
    }

    @Override
    public String identifier() {
        ResourceLocation id = id();
        return id == null ? this.englishName : id.toString();
    }

    @Override
    public T get() {
        return this.item.get();
    }

    @Override
    public T item() {
        return get();
    }

    @Override
    public T asItem() {
        return get();
    }

    public ItemStack stack() {
        return stack(1);
    }

    @Override
    public ItemStack stack(int stackSize) {
        return new ItemStack(get(), stackSize);
    }

    @Override
    public Set<AEFeature> features() {
        return Collections.emptySet();
    }

    @Override
    public boolean isSameAs(ItemStack comparableStack) {
        return comparableStack != null && !comparableStack.isEmpty() && comparableStack.getItem() == get();
    }

    @Override
    public boolean isSameAs(Item comparableItem) {
        return comparableItem == get();
    }
}
