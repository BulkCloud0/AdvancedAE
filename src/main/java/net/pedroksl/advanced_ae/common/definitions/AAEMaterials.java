package net.pedroksl.advanced_ae.common.definitions;

import java.util.EnumMap;
import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum AAEMaterials implements IArmorMaterial {
    QUANTUM_ALLOY(
            "quantum_alloy",
            10,
            protectionValues(4, 6, 9, 4),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            10.0F,
            0.25F,
            () -> Ingredient.fromItems(AAEItems.QUANTUM_ALLOY));

    private static final EnumMap<EquipmentSlotType, Integer> BASE_DURABILITY = durabilityValues();

    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<EquipmentSlotType, Integer> protectionBySlot;
    private final int enchantability;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    AAEMaterials(
            String name,
            int durabilityMultiplier,
            EnumMap<EquipmentSlotType, Integer> protectionBySlot,
            int enchantability,
            SoundEvent sound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionBySlot = protectionBySlot;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    private static EnumMap<EquipmentSlotType, Integer> durabilityValues() {
        EnumMap<EquipmentSlotType, Integer> values = new EnumMap<EquipmentSlotType, Integer>(EquipmentSlotType.class);
        values.put(EquipmentSlotType.FEET, 13);
        values.put(EquipmentSlotType.LEGS, 15);
        values.put(EquipmentSlotType.CHEST, 16);
        values.put(EquipmentSlotType.HEAD, 11);
        return values;
    }

    private static EnumMap<EquipmentSlotType, Integer> protectionValues(int feet, int legs, int chest, int head) {
        EnumMap<EquipmentSlotType, Integer> values = new EnumMap<EquipmentSlotType, Integer>(EquipmentSlotType.class);
        values.put(EquipmentSlotType.FEET, feet);
        values.put(EquipmentSlotType.LEGS, legs);
        values.put(EquipmentSlotType.CHEST, chest);
        values.put(EquipmentSlotType.HEAD, head);
        return values;
    }

    @Override
    public int getDurability(EquipmentSlotType slot) {
        Integer base = BASE_DURABILITY.get(slot);
        return (base == null ? 0 : base) * this.durabilityMultiplier;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slot) {
        Integer protection = this.protectionBySlot.get(slot);
        return protection == null ? 0 : protection;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
