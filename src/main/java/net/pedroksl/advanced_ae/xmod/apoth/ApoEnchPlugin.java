package net.pedroksl.advanced_ae.xmod.apoth;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ApoEnchPlugin {

    public enum Enchantment {
        STABLE_FOOTING
    }

    private static final ResourceLocation STABLE_FOOTING_ID = new ResourceLocation("apotheosis", "stable_footing");

    public static boolean isSameAs(net.minecraft.world.item.enchantment.Enchantment enchantment, Enchantment ench) {
        net.minecraft.world.item.enchantment.Enchantment target = getEnchantment(ench);
        return target != null && enchantment == target;
    }

    public static net.minecraft.world.item.enchantment.Enchantment getEnchantment(Enchantment enchantment) {
        switch (enchantment) {
            case STABLE_FOOTING:
                return ForgeRegistries.ENCHANTMENTS.getValue(STABLE_FOOTING_ID);
            default:
                return null;
        }
    }

    public static boolean checkForEnchant(Player player, Enchantment enchantment) {
        net.minecraft.world.item.enchantment.Enchantment target = getEnchantment(enchantment);
        if (target == null) {
            return false;
        }
        Iterable<ItemStack> armor = player.getArmorSlots();
        for (ItemStack stack : armor) {
            if (stack.getEnchantmentLevel(target) > 0) {
                return true;
            }
        }
        return false;
    }
}
