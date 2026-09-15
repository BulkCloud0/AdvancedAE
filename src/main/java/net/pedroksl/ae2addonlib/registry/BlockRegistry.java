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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;

/** Forge 1.16.5 registry backend used in place of newer AE2AddonLib. */
public class BlockRegistry {
    private static final Map<String, DeferredRegister<Block>> DEFERRED_REGISTERS = new HashMap<>();
    private static final Map<String, List<LibBlockDefinition<?>>> BLOCKS = new HashMap<>();

    private final String modId;

    public BlockRegistry(String modId) {
        this.modId = modId;
        if (DEFERRED_REGISTERS.containsKey(modId)) {
            throw new IllegalStateException("Block registry already initialized for " + modId);
        }
        DEFERRED_REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.BLOCKS, modId));
        BLOCKS.put(modId, new ArrayList<LibBlockDefinition<?>>());
    }

    static DeferredRegister<Block> getDR(String modId) {
        DeferredRegister<Block> register = DEFERRED_REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Block registry not initialized for " + modId);
        }
        return register;
    }

    public List<LibBlockDefinition<?>> getBlocks() {
        return getBlocks(this.modId);
    }

    public static List<LibBlockDefinition<?>> getBlocks(String modId) {
        List<LibBlockDefinition<?>> blocks = BLOCKS.get(modId);
        if (blocks == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(blocks);
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
        RegistryObject<T> deferredBlock = getDR(modId).register(id, blockSupplier);
        RegistryObject<BlockItem> deferredItem = ItemRegistry.getDR(modId).register(id, () -> {
            Block block = deferredBlock.get();
            Item.Properties itemProperties = new Item.Properties();
            if (itemFactory != null) {
                BlockItem item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException(
                            "BlockItem factory for " + new ResourceLocation(modId, id) + " returned null");
                }
                return item;
            }
            if (block instanceof AEBaseBlock) {
                return new AEBaseBlockItem(block, itemProperties);
            }
            return new BlockItem(block, itemProperties);
        });

        LibBlockDefinition<T> definition = new LibBlockDefinition<T>(
                englishName,
                deferredBlock,
                new LibItemDefinition<BlockItem>(englishName, deferredItem));
        BLOCKS.get(modId).add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
