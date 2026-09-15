package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import appeng.api.features.HotkeyAction;

public final class ToggleUpgradeCardAction implements HotkeyAction {
    private final Predicate<ItemStack> locatable;
    private final Opener opener;

    public ToggleUpgradeCardAction(ItemLike item, Opener opener) {
        this((stack) -> stack.is(item.asItem()), opener);
    }

    public ToggleUpgradeCardAction(Predicate<ItemStack> locatable, Opener opener) {
        this.locatable = locatable;
        this.opener = opener;
    }

    @Override
    public boolean run(Player player) {
        Iterable<ItemStack> items = player.getArmorSlots();
        for (ItemStack item : items) {
            if (this.locatable.test(item) && opener.open(player, item)) {
                return true;
            }
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
        boolean open(Player player, ItemStack stack);
    }
}
