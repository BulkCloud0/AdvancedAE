package net.pedroksl.advanced_ae.common.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

/**
 * Lightweight Forge 1.16.5 Quantum Armor item.
 *
 * Stores tint and upgrade configuration in NBT while the heavier ability,
 * energy, menu and rendering layers are ported independently.
 */
public class QuantumArmorItem extends ArmorItem {
    public static final int DEFAULT_TINT_COLOR = AAEColor.PURPLE.argb();

    private final Set<UpgradeType> allowedUpgrades;

    public QuantumArmorItem(
            IArmorMaterial material,
            EquipmentSlotType slot,
            Item.Properties properties,
            UpgradeType... allowedUpgrades) {
        super(material, slot, properties);
        EnumSet<UpgradeType> upgrades = EnumSet.noneOf(UpgradeType.class);
        Collections.addAll(upgrades, allowedUpgrades);
        upgrades.remove(UpgradeType.EMPTY);
        this.allowedUpgrades = Collections.unmodifiableSet(upgrades);
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

    public Set<UpgradeType> getAllowedUpgrades() {
        return allowedUpgrades;
    }

    public boolean isUpgradeAllowed(UpgradeType type) {
        return type != null && type != UpgradeType.EMPTY && allowedUpgrades.contains(type);
    }

    public List<UpgradeType> getAppliedUpgrades(ItemStack stack) {
        List<UpgradeType> result = new ArrayList<UpgradeType>();
        for (UpgradeType type : allowedUpgrades) {
            if (hasUpgrade(stack, type)) {
                result.add(type);
            }
        }
        return result;
    }

    public boolean hasUpgrade(ItemStack stack, UpgradeType type) {
        return stack.hasTag()
                && isUpgradeAllowed(type)
                && stack.getTag().getBoolean(upgradeKey(type, AAENbt.UPGRADE_INSTALLED));
    }

    public boolean applyUpgrade(ItemStack stack, UpgradeType type) {
        if (!isUpgradeAllowed(type) || hasUpgrade(stack, type)) {
            return false;
        }
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_INSTALLED), true);
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_TOGGLE), true);
        stack.getOrCreateTag().putInt(upgradeKey(type, AAENbt.UPGRADE_VALUE), 0);
        if (type.getExtraSettings() != UpgradeType.ExtraSettings.NONE) {
            stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_EXTRA), true);
        }
        return true;
    }

    public boolean removeUpgrade(ItemStack stack, UpgradeType type) {
        if (!hasUpgrade(stack, type)) {
            return false;
        }
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_INSTALLED), false);
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_TOGGLE), false);
        return true;
    }

    public boolean isUpgradeEnabled(ItemStack stack, UpgradeType type) {
        return hasUpgrade(stack, type)
                && stack.getTag().getBoolean(upgradeKey(type, AAENbt.UPGRADE_TOGGLE));
    }

    public boolean setUpgradeEnabled(ItemStack stack, UpgradeType type, boolean enabled) {
        if (!hasUpgrade(stack, type)) {
            return false;
        }
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_TOGGLE), enabled);
        return true;
    }

    public boolean toggleUpgrade(ItemStack stack, UpgradeType type) {
        if (!hasUpgrade(stack, type)) {
            return false;
        }
        return setUpgradeEnabled(stack, type, !isUpgradeEnabled(stack, type));
    }

    public int getUpgradeValue(ItemStack stack, UpgradeType type, int defaultValue) {
        if (!hasUpgrade(stack, type)) {
            return defaultValue;
        }
        String key = upgradeKey(type, AAENbt.UPGRADE_VALUE);
        return stack.getTag().contains(key) ? stack.getTag().getInt(key) : defaultValue;
    }

    public boolean setUpgradeValue(ItemStack stack, UpgradeType type, int value) {
        if (!hasUpgrade(stack, type)) {
            return false;
        }
        stack.getOrCreateTag().putInt(upgradeKey(type, AAENbt.UPGRADE_VALUE), value);
        return true;
    }

    public boolean getUpgradeExtra(ItemStack stack, UpgradeType type, boolean defaultValue) {
        if (!hasUpgrade(stack, type)) {
            return defaultValue;
        }
        String key = upgradeKey(type, AAENbt.UPGRADE_EXTRA);
        return stack.getTag().contains(key) ? stack.getTag().getBoolean(key) : defaultValue;
    }

    public boolean setUpgradeExtra(ItemStack stack, UpgradeType type, boolean value) {
        if (!hasUpgrade(stack, type) || type.getExtraSettings() == UpgradeType.ExtraSettings.NONE) {
            return false;
        }
        stack.getOrCreateTag().putBoolean(upgradeKey(type, AAENbt.UPGRADE_EXTRA), value);
        return true;
    }

    private static String upgradeKey(UpgradeType type, String field) {
        return "aae_upgrade_" + type.name().toLowerCase(Locale.ROOT) + "_" + field;
    }
}
