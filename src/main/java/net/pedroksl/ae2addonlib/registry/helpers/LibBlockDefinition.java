package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

/** 1.16.5 block definition retaining AE2AddonLib's source-level API. */
public class LibBlockDefinition<T extends Block> implements IItemProvider {
    private final String englishName;
    private final RegistryObject<T> block;
    private final LibItemDefinition<? extends BlockItem> item;

    public LibBlockDefinition(
            String englishName,
            RegistryObject<T> block,
            LibItemDefinition<? extends BlockItem> item) {
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

    public ResourceLocation getId() {
        return id();
    }

    public T block() {
        return this.block.get();
    }

    public T get() {
        return block();
    }

    public ItemStack stack() {
        return this.item.stack();
    }

    public ItemStack stack(int stackSize) {
        return this.item.stack(stackSize);
    }

    public boolean is(ItemStack comparableStack) {
        return this.item.isSameAs(comparableStack);
    }

    public LibItemDefinition<? extends BlockItem> item() {
        return this.item;
    }

    public BlockItem blockItem() {
        return this.item.get();
    }

    @Override
    public Item asItem() {
        return this.item.asItem();
    }
}
