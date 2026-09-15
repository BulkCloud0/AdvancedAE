package net.pedroksl.ae2addonlib.registry.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

/** Minimal 1.16.5-compatible block definition used by AdvancedAE. */
public class LibBlockDefinition<T extends Block> implements IItemProvider {
    private final String englishName;
    private final RegistryObject<T> block;
    private final LibItemDefinition<BlockItem> item;

    public LibBlockDefinition(
            String englishName, RegistryObject<T> block, LibItemDefinition<BlockItem> item) {
        this.englishName = englishName;
        this.block = block;
        this.item = item;
    }

    public String getEnglishName() {
        return englishName;
    }

    public ResourceLocation id() {
        return block.getId();
    }

    public T block() {
        return block.get();
    }

    public LibItemDefinition<BlockItem> item() {
        return item;
    }

    public ItemStack stack() {
        return item.stack();
    }

    public ItemStack stack(int amount) {
        return item.stack(amount);
    }

    public boolean isSameAs(ItemStack stack) {
        return item.isSameAs(stack);
    }

    @Override
    public BlockItem asItem() {
        return item.get();
    }
}
