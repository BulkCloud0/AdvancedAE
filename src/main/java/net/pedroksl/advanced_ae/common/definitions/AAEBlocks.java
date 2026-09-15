package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.*;
import net.pedroksl.advanced_ae.common.items.AAECraftingBlockItem;
import net.pedroksl.ae2addonlib.registry.BlockRegistry;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;

import appeng.block.AEBaseBlockItem;
import appeng.decorative.AEDecorativeBlock;

public final class AAEBlocks extends BlockRegistry {

    public static final AAEBlocks INSTANCE = new AAEBlocks();

    AAEBlocks() {
        super(AdvancedAE.MOD_ID);
    }

    public static final LibBlockDefinition<AEDecorativeBlock> QUANTUM_ALLOY_BLOCK = block(
            "Quantum Alloy Block",
            "quantum_alloy_block",
            () -> new AEDecorativeBlock(stoneProps().hardnessAndResistance(25.0f, 150.0f)),
            BlockItem::new);
    public static final LibBlockDefinition<StairsBlock> QUANTUM_ALLOY_STAIRS = block(
            "Quantum Alloy Stairs",
            "quantum_alloy_stairs",
            () -> new StairsBlock(() -> QUANTUM_ALLOY_BLOCK.block().getDefaultState(), metalProps()),
            BlockItem::new);
    public static final LibBlockDefinition<WallBlock> QUANTUM_ALLOY_WALL =
            block("Quantum Alloy Wall", "quantum_alloy_wall", () -> new WallBlock(metalProps()), BlockItem::new);
    public static final LibBlockDefinition<SlabBlock> QUANTUM_ALLOY_SLAB =
            block("Quantum Alloy Slab", "quantum_alloy_slab", () -> new SlabBlock(metalProps()), BlockItem::new);

    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_UNIT = block(
            "Quantum Crafting Unit",
            "quantum_unit",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_UNIT),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_CORE = block(
            "Quantum Computer Core",
            "quantum_core",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_CORE),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_128M = block(
            "128M Quantum Computer Storage",
            "quantum_storage_128",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_128M),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_256M = block(
            "256M Quantum Computer Storage",
            "quantum_storage_256",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_256M),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> DATA_ENTANGLER = block(
            "Quantum Data Entangler",
            "data_entangler",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_MULTIPLIER),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_ACCELERATOR = block(
            "Quantum Computer Accelerator",
            "quantum_accelerator",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_ACCELERATOR),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_MULTI_THREADER = block(
            "Quantum Computer Multi-Threader",
            "quantum_multi_threader",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.MULTI_THREADER),
            AAECraftingBlockItem::new);
    public static final LibBlockDefinition<AAECraftingUnitBlock> QUANTUM_STRUCTURE = block(
            "Quantum Computer Structural Glass",
            "quantum_structure",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STRUCTURE),
            AAECraftingBlockItem::new);

    public static final LibBlockDefinition<AdvPatternProviderBlock> ADV_PATTERN_PROVIDER = block(
            "Advanced Extended Pattern Provider",
            "adv_pattern_provider",
            AdvPatternProviderBlock::new,
            AEBaseBlockItem::new);
    public static final LibBlockDefinition<SmallAdvPatternProviderBlock> SMALL_ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider",
            "small_adv_pattern_provider",
            SmallAdvPatternProviderBlock::new,
            AEBaseBlockItem::new);

    public static final LibBlockDefinition<ReactionChamberBlock> REACTION_CHAMBER =
            block("Reaction Chamber", "reaction_chamber", ReactionChamberBlock::new, AEBaseBlockItem::new);
    public static final LibBlockDefinition<QuantumCrafterBlock> QUANTUM_CRAFTER =
            block("Quantum Crafter", "quantum_crafter", QuantumCrafterBlock::new, AEBaseBlockItem::new);

    private static AbstractBlock.Properties stoneProps() {
        return AbstractBlock.Properties.create(Material.ROCK)
                .hardnessAndResistance(2.2f, 11.0f)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(0);
    }

    private static AbstractBlock.Properties metalProps() {
        return AbstractBlock.Properties.create(Material.IRON)
                .hardnessAndResistance(2.2f, 11.0f)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(0);
    }

    protected static <T extends Block> LibBlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        return block(AdvancedAE.MOD_ID, englishName, id, blockSupplier, null);
    }

    protected static <T extends Block> LibBlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        return block(AdvancedAE.MOD_ID, englishName, id, blockSupplier, itemFactory);
    }
}
