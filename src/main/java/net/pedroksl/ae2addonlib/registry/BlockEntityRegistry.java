package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.IStackSrc;
import appeng.tile.AEBaseTileEntity;

/**
 * Minimal Forge 1.16.5 replacement for the AE2AddonLib block-entity registry.
 *
 * AdvancedAE's 1.20 source passes position/state into block-entity constructors.
 * Minecraft 1.16 tile entities are constructed from their type only, so the
 * compatibility factory keeps the old call-site shape while using placeholder
 * position/state values. Ported entity constructors must ignore those two values
 * and delegate to the AE2 8 TileEntityType-only constructor.
 */
public class BlockEntityRegistry {
    private static final Map<String, DeferredRegister<TileEntityType<?>>> REGISTERS = new HashMap<>();
    private static final Map<String, List<TileEntityType<?>>> TYPES = new HashMap<>();

    private final String modId;

    public BlockEntityRegistry(String modId) {
        this.modId = modId;
        if (!REGISTERS.containsKey(modId)) {
            REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, modId));
            TYPES.put(modId, new ArrayList<>());
        }
    }

    private static DeferredRegister<TileEntityType<?>> getRegister(String modId) {
        DeferredRegister<TileEntityType<?>> register = REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Tile entity registry not initialized for mod " + modId);
        }
        return register;
    }

    public List<TileEntityType<?>> getTypes() {
        List<TileEntityType<?>> types = TYPES.get(modId);
        return types == null ? Collections.emptyList() : Collections.unmodifiableList(types);
    }

    @SafeVarargs
    protected static <T extends AEBaseTileEntity> Supplier<TileEntityType<T>> create(
            String modId,
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            LibBlockDefinition<? extends Block>... blockDefs) {
        if (blockDefs.length == 0) {
            throw new IllegalArgumentException("At least one block is required for tile entity " + id);
        }

        return getRegister(modId).register(id, () -> {
            Block[] blocks = Arrays.stream(blockDefs)
                    .map(LibBlockDefinition::block)
                    .toArray(Block[]::new);

            AtomicReference<TileEntityType<T>> typeRef = new AtomicReference<>();
            Supplier<T> tileFactory = () -> factory.create(
                    typeRef.get(), BlockPos.ZERO, Blocks.AIR.getDefaultState());

            TileEntityType<T> type = TileEntityType.Builder.create(tileFactory, blocks).build(null);
            typeRef.set(type);
            TYPES.get(modId).add(type);

            for (Block block : blocks) {
                if (block instanceof AEBaseTileBlock) {
                    @SuppressWarnings("rawtypes")
                    AEBaseTileBlock aeBlock = (AEBaseTileBlock) block;
                    aeBlock.setTileEntity(entityClass, tileFactory);
                }
            }

            final LibBlockDefinition<? extends Block> primaryBlock = blockDefs[0];
            AEBaseTileEntity.registerTileItem(entityClass, new IStackSrc() {
                @Override
                public ItemStack stack(int amount) {
                    return primaryBlock.stack(amount);
                }

                @Override
                public Item getItem() {
                    return primaryBlock.asItem();
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            });

            return type;
        });
    }

    public void register(IEventBus eventBus) {
        getRegister(modId).register(eventBus);
    }

    protected interface BlockEntityFactory<T extends AEBaseTileEntity> {
        T create(TileEntityType<T> type, BlockPos pos, BlockState state);
    }
}
