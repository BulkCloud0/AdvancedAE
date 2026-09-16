package net.pedroksl.advanced_ae.common.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.QuantumArmorItem;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.common.parts.AdvancedIOBusPart;
import net.pedroksl.advanced_ae.common.parts.ImportExportBusPart;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;
import net.pedroksl.ae2addonlib.registry.ItemRegistry;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.api.parts.IPart;
import appeng.items.parts.PartItem;

public class AAEItems extends ItemRegistry {
    public static AAEItems INSTANCE = new AAEItems();
    AAEItems() { super(AdvancedAE.MOD_ID); }

    public static List<LibItemDefinition<?>> getQuantumArmor() {
        return Arrays.<LibItemDefinition<?>>asList(QUANTUM_HELMET, QUANTUM_CHESTPLATE, QUANTUM_LEGGINGS, QUANTUM_BOOTS);
    }

    public static List<LibItemDefinition<?>> getQuantumCards() {
        return Arrays.<LibItemDefinition<?>>asList(
                QUANTUM_UPGRADE_BASE, WALK_SPEED_CARD, SPRINT_SPEED_CARD, STEP_ASSIST_CARD, JUMP_HEIGHT_CARD,
                LAVA_IMMUNITY_CARD, FLIGHT_CARD, WATER_BREATHING_CARD, AUTO_FEED_CARD, AUTO_STOCK_CARD,
                MAGNET_CARD, HP_BUFFER_CARD, EVASION_CARD, REGENERATION_CARD, STRENGTH_CARD,
                ATTACK_SPEED_CARD, LUCK_CARD, REACH_CARD, SWIM_SPEED_CARD, NIGHT_VISION_CARD,
                FLIGHT_DRIFT_CARD, RECHARGING_CARD, WORKBENCH_CARD, PICK_CRAFT_CARD);
    }

    public static final LibItemDefinition<Item> ADV_PATTERN_PROVIDER = item("Advanced Extended Pattern Provider", "adv_pattern_provider_part", Item::new);
    public static final LibItemDefinition<Item> SMALL_ADV_PATTERN_PROVIDER = item("Advanced Pattern Provider", "small_adv_pattern_provider_part", Item::new);
    public static final LibItemDefinition<PartItem<StockExportBusPart>> STOCK_EXPORT_BUS = part("ME Stock Export Bus", "stock_export_bus_part", StockExportBusPart.class, StockExportBusPart::new);
    public static final LibItemDefinition<PartItem<ImportExportBusPart>> IMPORT_EXPORT_BUS = part("ME Import Export Bus", "import_export_bus_part", ImportExportBusPart.class, ImportExportBusPart::new);
    public static final LibItemDefinition<PartItem<AdvancedIOBusPart>> ADVANCED_IO_BUS = part("ME Advanced IO Bus", "advanced_io_bus_part", AdvancedIOBusPart.class, AdvancedIOBusPart::new);
    public static final LibItemDefinition<Item> THROUGHPUT_MONITOR = item("ME Throughput Monitor", "throughput_monitor", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_CRAFTER_TERMINAL = item("Quantum Crafter Terminal", "quantum_crafter_terminal", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_CRAFTER_WIRELESS_TERMINAL = item("Wireless Quantum Crafter Terminal", "wireless_quantum_crafter_terminal", Item::new);
    public static final LibItemDefinition<Item> ADV_PROCESSING_PATTERN = item("Advanced Processing Pattern", "adv_processing_pattern", Item::new);
    public static final LibItemDefinition<Item> ADV_PATTERN_PROVIDER_UPGRADE = item("Advanced Pattern Provider Upgrade", "adv_pattern_provider_upgrade", Item::new);
    public static final LibItemDefinition<Item> ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE = item("Advanced Pattern Provider Capacity Upgrade", "adv_pattern_provider_capacity_upgrade", Item::new);

    public static final LibItemDefinition<Item> QUANTUM_INFUSED_DUST = item("Quantum Infused Dust", "quantum_infused_dust", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_ALLOY = item("Quantum Alloy", "quantum_alloy", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_ALLOY_PLATE = item("Quantum Alloy Plate", "quantum_alloy_plate", p -> new Item(p.rarity(Rarity.EPIC)));
    public static final LibItemDefinition<Item> SHATTERED_SINGULARITY = item("Shattered Singularity", "shattered_singularity", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_PROCESSOR_PRESS = item("Inscriber Quantum Press", "quantum_processor_press", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_PROCESSOR_PRINT = item("Printed Quantum Circuit", "printed_quantum_processor", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_PROCESSOR = item("Quantum Processor", "quantum_processor", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_STORAGE_COMPONENT = item("Quantum Storage Component", "quantum_storage_component", Item::new);
    public static final LibItemDefinition<Item> ADV_PATTERN_ENCODER = item("Advanced Pattern Encoder", "adv_pattern_encoder", Item::new);
    public static final LibItemDefinition<Item> MONITOR_CONFIGURATOR = item("Throughput Monitor Configurator", "throughput_monitor_configurator", Item::new);

    public static final LibItemDefinition<QuantumArmorItem> QUANTUM_HELMET = item(
            "Quantum Helmet", "quantum_helmet",
            p -> new QuantumArmorItem(
                    AAEMaterials.QUANTUM_ALLOY, EquipmentSlotType.HEAD, p.rarity(Rarity.EPIC),
                    UpgradeType.WATER_BREATHING, UpgradeType.AUTO_FEED, UpgradeType.AUTO_STOCK,
                    UpgradeType.MAGNET, UpgradeType.LUCK, UpgradeType.NIGHT_VISION,
                    UpgradeType.CHARGING, UpgradeType.WORKBENCH));
    public static final LibItemDefinition<QuantumArmorItem> QUANTUM_CHESTPLATE = item(
            "Quantum Chestplate", "quantum_chestplate",
            p -> new QuantumArmorItem(
                    AAEMaterials.QUANTUM_ALLOY, EquipmentSlotType.CHEST, p.rarity(Rarity.EPIC),
                    UpgradeType.FLIGHT, UpgradeType.HP_BUFFER, UpgradeType.LAVA_IMMUNITY,
                    UpgradeType.REGENERATION, UpgradeType.STRENGTH, UpgradeType.ATTACK_SPEED,
                    UpgradeType.CHARGING, UpgradeType.PICK_CRAFT));
    public static final LibItemDefinition<QuantumArmorItem> QUANTUM_LEGGINGS = item(
            "Quantum Leggings", "quantum_leggings",
            p -> new QuantumArmorItem(
                    AAEMaterials.QUANTUM_ALLOY, EquipmentSlotType.LEGS, p.rarity(Rarity.EPIC),
                    UpgradeType.WALK_SPEED, UpgradeType.SPRINT_SPEED, UpgradeType.SWIM_SPEED,
                    UpgradeType.REACH, UpgradeType.CHARGING));
    public static final LibItemDefinition<QuantumArmorItem> QUANTUM_BOOTS = item(
            "Quantum Boots", "quantum_boots",
            p -> new QuantumArmorItem(
                    AAEMaterials.QUANTUM_ALLOY, EquipmentSlotType.FEET, p.rarity(Rarity.EPIC),
                    UpgradeType.STEP_ASSIST, UpgradeType.JUMP_HEIGHT, UpgradeType.EVASION,
                    UpgradeType.FLIGHT_DRIFT, UpgradeType.CHARGING));

    public static final LibItemDefinition<QuantumUpgradeBaseItem> QUANTUM_UPGRADE_BASE = upgrade(
            "Quantum Upgrade Base Card", "quantum_upgrade_base", UpgradeType.EMPTY);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> WALK_SPEED_CARD = upgrade(
            "Walk Speed Card", "walk_speed_card", UpgradeType.WALK_SPEED);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> SPRINT_SPEED_CARD = upgrade(
            "Sprint Speed Card", "sprint_speed_card", UpgradeType.SPRINT_SPEED);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> STEP_ASSIST_CARD = upgrade(
            "Step Assist Card", "step_assist_card", UpgradeType.STEP_ASSIST);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> JUMP_HEIGHT_CARD = upgrade(
            "Jump Height Card", "jump_height_card", UpgradeType.JUMP_HEIGHT);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> LAVA_IMMUNITY_CARD = upgrade(
            "Lava Immunity Card", "lava_immunity_card", UpgradeType.LAVA_IMMUNITY);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> FLIGHT_CARD = upgrade(
            "Flight Card", "flight_card", UpgradeType.FLIGHT);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> WATER_BREATHING_CARD = upgrade(
            "Water Breathing Card", "water_breathing_card", UpgradeType.WATER_BREATHING);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> AUTO_FEED_CARD = upgrade(
            "Auto Feeding Card", "auto_feeding_card", UpgradeType.AUTO_FEED);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> AUTO_STOCK_CARD = upgrade(
            "Auto Stock Card", "auto_stock_card", UpgradeType.AUTO_STOCK);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> MAGNET_CARD = upgrade(
            "Magnet Card", "magnet_card", UpgradeType.MAGNET);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> HP_BUFFER_CARD = upgrade(
            "HP Buffer Card", "hp_buffer_card", UpgradeType.HP_BUFFER);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> EVASION_CARD = upgrade(
            "Evasion Card", "evasion_card", UpgradeType.EVASION);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> REGENERATION_CARD = upgrade(
            "Regeneration Card", "regeneration_card", UpgradeType.REGENERATION);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> STRENGTH_CARD = upgrade(
            "Strength Card", "strength_card", UpgradeType.STRENGTH);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> ATTACK_SPEED_CARD = upgrade(
            "Attack Speed Card", "attack_speed_card", UpgradeType.ATTACK_SPEED);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> LUCK_CARD = upgrade(
            "Luck Card", "luck_card", UpgradeType.LUCK);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> REACH_CARD = upgrade(
            "Reach Card", "reach_card", UpgradeType.REACH);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> SWIM_SPEED_CARD = upgrade(
            "Swim Speed Card", "swim_speed_card", UpgradeType.SWIM_SPEED);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> NIGHT_VISION_CARD = upgrade(
            "Night Vision Card", "night_vision_card", UpgradeType.NIGHT_VISION);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> FLIGHT_DRIFT_CARD = upgrade(
            "Flight Drift Card", "flight_drift_card", UpgradeType.FLIGHT_DRIFT);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> RECHARGING_CARD = upgrade(
            "ME Recharging Card", "recharging_card", UpgradeType.CHARGING);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> WORKBENCH_CARD = upgrade(
            "Portable Workbench Card", "portable_workbench_card", UpgradeType.WORKBENCH);
    public static final LibItemDefinition<QuantumUpgradeBaseItem> PICK_CRAFT_CARD = upgrade(
            "Pick Craft Card", "pick_craft_card", UpgradeType.PICK_CRAFT);

    protected static <T extends Item> LibItemDefinition<T> item(String englishName, String id, Function<Item.Properties, T> factory) {
        return item(AdvancedAE.MOD_ID, englishName, id, factory);
    }

    private static LibItemDefinition<QuantumUpgradeBaseItem> upgrade(
            String englishName, String id, UpgradeType type) {
        return item(englishName, id, properties -> new QuantumUpgradeBaseItem(type, properties));
    }

    protected static <T extends IPart> LibItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<ItemStack, T> factory) {
        return part(AdvancedAE.MOD_ID, englishName, id, partClass, factory);
    }
}
