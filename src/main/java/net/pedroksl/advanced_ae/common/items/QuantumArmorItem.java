package net.pedroksl.advanced_ae.common.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;

/**
 * Lightweight Forge 1.16.5 Quantum Armor item.
 *
 * This restores item-owned persistent state without pulling the modern
 * rendering/menu/ability stack back into the baseline prematurely.
 */
public class QuantumArmorItem extends ArmorItem {
    public static final int DEFAULT_TINT_COLOR = AAEColor.PURPLE.argb();

    public QuantumArmorItem(IArmorMaterial material, EquipmentSlotType slot, Item.Properties properties) {
        super(material, slot, properties);
    }

    public int getTintColor(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(AAENbt.TINT_COLOR_TAG)) {
            return stack.getTag().getInt(AAENbt.TINT_COLOR_TAG);
        }
        return DEFAULT_TINT_COLOR;
    }

    public void setTintColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt(AAENbt.TINT_COLOR_TAG, color);
    }
}
