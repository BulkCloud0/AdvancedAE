package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.api.parts.IPart;
import appeng.items.parts.PartItem;

/**
 * Forge 1.16.5 registry backend used in place of the newer AE2AddonLib artifact.
 */
public class ItemRegistry {
    private static final Map<String, DeferredRegister<Item>> DEFERRED_REGISTERS = new HashMap<>();
    private static final Map<String, List<LibItemDefinition<?>>> ITEMS = new HashMap<>();

    private final String modId;

    public ItemRegistry(String modId) {
        this.modId = modId;
        if (DEFERRED_REGISTERS.containsKey(modId)) {
            throw new IllegalStateException("Item registry already initialized for " + modId);
        }
        DEFERRED_REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.ITEMS, modId));
        ITEMS.put(modId, new ArrayList<LibItemDefinition<?>>());
    }

    static DeferredRegister<Item> getDR(String modId) {
        DeferredRegister<Item> register = DEFERRED_REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Item registry not initialized for " + modId);
        }
        return register;
    }

    public List<LibItemDefinition<?>> getItems() {
        return getItems(this.modId);
    }

    public static List<LibItemDefinition<?>> getItems(String modId) {
        List<LibItemDefinition<?>> items = ITEMS.get(modId);
        if (items == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(items);
    }

    protected static <T extends Item> LibItemDefinition<T> item(
            String modId, String englishName, String id, Function<Item.Properties, T> factory) {
        LibItemDefinition<T> definition = new LibItemDefinition<T>(
                englishName,
                getDR(modId).register(id, () -> factory.apply(new Item.Properties())));
        ITEMS.get(modId).add(definition);
        return definition;
    }

    /**
     * AE2 8.x creates parts from the placed ItemStack rather than IPartItem.
     */
    protected static <T extends IPart> LibItemDefinition<PartItem<T>> part(
            String modId, String englishName, String id, Function<ItemStack, T> factory) {
        return item(modId, englishName, id, properties -> new PartItem<T>(properties, factory));
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
