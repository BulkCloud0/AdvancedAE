package net.pedroksl.ae2addonlib.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;

import appeng.block.AEBaseTileBlock;

/** Forge 1.16.5 tile entity registry backend used in place of newer AE2AddonLib. */
public class BlockEntityRegistry {
    private static final Map<String, DeferredRegister<TileEntityType<?>>> DEFERRED_REGISTERS = new HashMap<>();

    private final String modId;

    public BlockEntityRegistry(String modId) {
        this.modId = modId;
        if (DEFERRED_REGISTERS.containsKey(modId)) {
            throw new IllegalStateException("Block entity registry already initialized for " + modId);
        }
        DEFERRED_REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, modId));
    }

    static DeferredRegister<TileEntityType<?>> getDR(String modId) {
        DeferredRegister<TileEntityType<?>> register = DEFERRED_REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Block entity registry not initialized for " + modId);
        }
        return register;
    }

    @SafeVarargs
    protected static <T extends TileEntity> Supplier<TileEntityType<T>> create(
            String modId,
            String id,
            Class<T> entityClass,
            Supplier<T> factory,
            LibBlockDefinition<? extends Block>... blockDefs) {
        RegistryObject<TileEntityType<T>> type = getDR(modId).register(id, () -> {
            Block[] blocks = new Block[blockDefs.length];
            for (int i = 0; i < blockDefs.length; i++) {
                Block block = blockDefs[i].block();
                blocks[i] = block;
                if (block instanceof AEBaseTileBlock) {
                    @SuppressWarnings("unchecked")
                    AEBaseTileBlock<T> tileBlock = (AEBaseTileBlock<T>) block;
                    tileBlock.setTileEntity(entityClass, factory);
                }
            }
            return TileEntityType.Builder.create(factory::get, blocks).build(null);
        });
        return type;
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
