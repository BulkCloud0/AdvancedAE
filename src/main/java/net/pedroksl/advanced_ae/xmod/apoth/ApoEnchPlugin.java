package net.pedroksl.advanced_ae.xmod.apoth;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import dev.shadowsoffire.apotheosis.ench.enchantments.StableFootingEnchant;

public class ApoEnchPlugin {

    public enum Enchantment {
        STABLE_FOOTING
    }

    public static boolean isSameAs(net.minecraft.world.item.enchantment.Enchantment enchantment, Enchantment ench) {
        switch (ench) {
            case STABLE_FOOTING:
                return enchantment instanceof StableFootingEnchant;
            default:
                return false;
        }
    }

    public static net.minecraft.world.item.enchantment.Enchantment getEnchantment(Enchantment enchantment) {
        switch (enchantment) {
            case STABLE_FOOTING:
                return new StableFootingEnchant();
            default:
                throw new IllegalArgumentException("Unsupported enchantment: " + enchantment);
        }
    }

    public static boolean checkForEnchant(Player player, Enchantment enchantment) {
        Iterable<ItemStack> armor = player.getArmorSlots();
        for (ItemStack stack : armor) {
            if (stack.getEnchantmentLevel(getEnchantment(enchantment)) > 0) {
                return true;
            }
        }
        return false;
    }
}
