package net.pedroksl.ae2addonlib.registry;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Forge 1.16.5 compatibility replacement for AE2AddonLib's creative-tab registry.
 * Item groups are constructed directly in 1.16.5 and are not Forge registry entries.
 */
public class CreativeTabRegistry {
    private final ItemGroup itemGroup;

    public CreativeTabRegistry(String modId, Object displayName, Supplier<ItemStack> iconSupplier) {
        Objects.requireNonNull(modId, "modId");
        Objects.requireNonNull(iconSupplier, "iconSupplier");

        this.itemGroup = new ItemGroup(modId) {
            @Override
            public ItemStack createIcon() {
                ItemStack icon = iconSupplier.get();
                return icon == null ? ItemStack.EMPTY : icon;
            }
        };
    }

    public ItemGroup get() {
        return this.itemGroup;
    }

    /**
     * Kept for source compatibility with newer AE2AddonLib. ItemGroup is not a
     * DeferredRegister-backed registry object on Minecraft 1.16.5.
     */
    public void register(IEventBus eventBus) {
        // No-op by design on 1.16.5.
    }
}
