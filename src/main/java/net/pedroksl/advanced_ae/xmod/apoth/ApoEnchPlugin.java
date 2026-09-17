package net.pedroksl.advanced_ae.xmod.apoth;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ApoEnchPlugin {

    public enum EnchantmentType {
        STABLE_FOOTING
    }

    private static final ResourceLocation STABLE_FOOTING_ID = new ResourceLocation("apotheosis", "stable_footing");

    public static boolean isSameAs(Enchantment enchantment, EnchantmentType ench) {
        Enchantment target = getEnchantment(ench);
        return target != null && enchantment == target;
    }

    public static Enchantment getEnchantment(EnchantmentType enchantment) {
        switch (enchantment) {
            case STABLE_FOOTING:
                return ForgeRegistries.ENCHANTMENTS.getValue(STABLE_FOOTING_ID);
            default:
                return null;
        }
    }

    public static boolean checkForEnchant(PlayerEntity player, EnchantmentType enchantment) {
        Enchantment target = getEnchantment(enchantment);
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
