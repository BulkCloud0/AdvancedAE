package net.pedroksl.advanced_ae.common.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.pedroksl.advanced_ae.AdvancedAE;
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

    public static final LibItemDefinition<Item> QUANTUM_HELMET = item("Quantum Helmet", "quantum_helmet", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_CHESTPLATE = item("Quantum Chestplate", "quantum_chestplate", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_LEGGINGS = item("Quantum Leggings", "quantum_leggings", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_BOOTS = item("Quantum Boots", "quantum_boots", Item::new);
    public static final LibItemDefinition<Item> QUANTUM_UPGRADE_BASE = item("Quantum Upgrade Base Card", "quantum_upgrade_base", Item::new);
    public static final LibItemDefinition<Item> WALK_SPEED_CARD = item("Walk Speed Card", "walk_speed_card", Item::new);
    public static final LibItemDefinition<Item> SPRINT_SPEED_CARD = item("Sprint Speed Card", "sprint_speed_card", Item::new);
    public static final LibItemDefinition<Item> STEP_ASSIST_CARD = item("Step Assist Card", "step_assist_card", Item::new);
    public static final LibItemDefinition<Item> JUMP_HEIGHT_CARD = item("Jump Height Card", "jump_height_card", Item::new);
    public static final LibItemDefinition<Item> LAVA_IMMUNITY_CARD = item("Lava Immunity Card", "lava_immunity_card", Item::new);
    public static final LibItemDefinition<Item> FLIGHT_CARD = item("Flight Card", "flight_card", Item::new);
    public static final LibItemDefinition<Item> WATER_BREATHING_CARD = item("Water Breathing Card", "water_breathing_card", Item::new);
    public static final LibItemDefinition<Item> AUTO_FEED_CARD = item("Auto Feeding Card", "auto_feeding_card", Item::new);
    public static final LibItemDefinition<Item> AUTO_STOCK_CARD = item("Auto Stock Card", "auto_stock_card", Item::new);
    public static final LibItemDefinition<Item> MAGNET_CARD = item("Magnet Card", "magnet_card", Item::new);
    public static final LibItemDefinition<Item> HP_BUFFER_CARD = item("HP Buffer Card", "hp_buffer_card", Item::new);
    public static final LibItemDefinition<Item> EVASION_CARD = item("Evasion Card", "evasion_card", Item::new);
    public static final LibItemDefinition<Item> REGENERATION_CARD = item("Regeneration Card", "regeneration_card", Item::new);
    public static final LibItemDefinition<Item> STRENGTH_CARD = item("Strength Card", "strength_card", Item::new);
    public static final LibItemDefinition<Item> ATTACK_SPEED_CARD = item("Attack Speed Card", "attack_speed_card", Item::new);
    public static final LibItemDefinition<Item> LUCK_CARD = item("Luck Card", "luck_card", Item::new);
    public static final LibItemDefinition<Item> REACH_CARD = item("Reach Card", "reach_card", Item::new);
    public static final LibItemDefinition<Item> SWIM_SPEED_CARD = item("Swim Speed Card", "swim_speed_card", Item::new);
    public static final LibItemDefinition<Item> NIGHT_VISION_CARD = item("Night Vision Card", "night_vision_card", Item::new);
    public static final LibItemDefinition<Item> FLIGHT_DRIFT_CARD = item("Flight Drift Card", "flight_drift_card", Item::new);
    public static final LibItemDefinition<Item> RECHARGING_CARD = item("ME Recharging Card", "recharging_card", Item::new);
    public static final LibItemDefinition<Item> WORKBENCH_CARD = item("Portable Workbench Card", "portable_workbench_card", Item::new);
    public static final LibItemDefinition<Item> PICK_CRAFT_CARD = item("Pick Craft Card", "pick_craft_card", Item::new);

    protected static <T extends Item> LibItemDefinition<T> item(String englishName, String id, Function<Item.Properties, T> factory) {
        return item(AdvancedAE.MOD_ID, englishName, id, factory);
    }

    protected static <T extends IPart> LibItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<ItemStack, T> factory) {
        return part(AdvancedAE.MOD_ID, englishName, id, partClass, factory);
    }
}
