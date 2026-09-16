package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import appeng.api.features.HotkeyAction;

/** Java 8 equivalent of the upstream record used by the 1.20.x codebase. */
public final class ArmorHotkeyAction implements HotkeyAction {
    private final Predicate<ItemStack> locatable;
    private final Opener opener;

    public ArmorHotkeyAction(ItemLike item, Opener opener) {
        this((stack) -> stack.is(item.asItem()), opener);
    }

    public ArmorHotkeyAction(Predicate<ItemStack> locatable, Opener opener) {
        this.locatable = locatable;
        this.opener = opener;
    }

    @Override
    public boolean run(Player player) {
        Iterable<ItemStack> items = player.getArmorSlots();
        int i = 0;
        for (ItemStack item : items) {
            if (this.locatable.test(item)
                    && this.opener.open(player, Inventory.INVENTORY_SIZE + i, item)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public Predicate<ItemStack> locatable() {
        return this.locatable;
    }

    public Opener opener() {
        return this.opener;
    }

    @FunctionalInterface
    public interface Opener {
        boolean open(Player player, int inventorySlot, ItemStack stack);
    }
}
