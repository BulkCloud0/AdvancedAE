package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/** Minimal 1.16.5 compatibility version of AE2AddonLib's block definition. */
public class LibBlockDefinition<T extends Block> implements IItemProvider {
    private final String englishName;
    private final RegistryObject<T> block;
    private final LibItemDefinition<BlockItem> item;

    public LibBlockDefinition(String englishName, RegistryObject<T> block, LibItemDefinition<BlockItem> item) {
        this.englishName = englishName;
        this.block = Objects.requireNonNull(block, "block");
        this.item = Objects.requireNonNull(item, "item");
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public ResourceLocation id() {
        return this.block.getId();
    }

    public T block() {
        return this.block.get();
    }

    public ItemStack stack() {
        return this.item.stack();
    }

    public ItemStack stack(int stackSize) {
        return this.item.stack(stackSize);
    }

    public boolean is(ItemStack comparableStack) {
        return this.item.is(comparableStack);
    }

    public LibItemDefinition<BlockItem> item() {
        return this.item;
    }

    @Override
    public BlockItem asItem() {
        return this.item.asItem();
    }
}
