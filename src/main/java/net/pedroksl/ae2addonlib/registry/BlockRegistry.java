package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;

/** Minimal Forge 1.16.5 registry shim for the subset of AE2AddonLib used by AdvancedAE. */
public class BlockRegistry {
    private static final Map<String, DeferredRegister<Block>> REGISTERS = new HashMap<>();
    private static final Map<String, List<LibBlockDefinition<?>>> BLOCKS = new HashMap<>();

    private final String modId;

    public BlockRegistry(String modId) {
        this.modId = modId;
        if (!REGISTERS.containsKey(modId)) {
            REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.BLOCKS, modId));
            BLOCKS.put(modId, new ArrayList<>());
        }
    }

    private static DeferredRegister<Block> getRegister(String modId) {
        DeferredRegister<Block> register = REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Block registry not initialized for mod " + modId);
        }
        return register;
    }

    public List<LibBlockDefinition<?>> getBlocks() {
        List<LibBlockDefinition<?>> blocks = BLOCKS.get(modId);
        return blocks == null ? Collections.emptyList() : Collections.unmodifiableList(blocks);
    }

    protected static <T extends Block> LibBlockDefinition<T> block(
            String modId, String englishName, String id, Supplier<T> blockSupplier) {
        return block(modId, englishName, id, blockSupplier, null);
    }

    protected static <T extends Block> LibBlockDefinition<T> block(
            String modId,
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        RegistryObject<T> block = getRegister(modId).register(id, blockSupplier);
        LibItemDefinition<BlockItem> item = ItemRegistry.registerBlockItem(modId, englishName, id, () -> {
            Block instance = block.get();
            Item.Properties properties = new Item.Properties();
            if (itemFactory != null) {
                return itemFactory.apply(instance, properties);
            }
            if (instance instanceof AEBaseBlock) {
                return new AEBaseBlockItem(instance, properties);
            }
            return new BlockItem(instance, properties);
        });

        LibBlockDefinition<T> definition = new LibBlockDefinition<>(englishName, block, item);
        BLOCKS.get(modId).add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        getRegister(modId).register(eventBus);
    }
}
