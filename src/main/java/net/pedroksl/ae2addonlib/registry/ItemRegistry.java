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
import appeng.api.parts.IPartItem;
import appeng.items.parts.PartItem;

/** Minimal Forge 1.16.5 registry shim for the subset of AE2AddonLib used by AdvancedAE. */
public class ItemRegistry {
    private static final Map<String, DeferredRegister<Item>> REGISTERS = new HashMap<>();
    private static final Map<String, List<LibItemDefinition<?>>> ITEMS = new HashMap<>();

    private final String modId;

    public ItemRegistry(String modId) {
        this.modId = modId;
        if (!REGISTERS.containsKey(modId)) {
            REGISTERS.put(modId, DeferredRegister.create(ForgeRegistries.ITEMS, modId));
            ITEMS.put(modId, new ArrayList<>());
        }
    }

    private static DeferredRegister<Item> getRegister(String modId) {
        DeferredRegister<Item> register = REGISTERS.get(modId);
        if (register == null) {
            throw new IllegalStateException("Item registry not initialized for mod " + modId);
        }
        return register;
    }

    public List<LibItemDefinition<?>> getItems() {
        return getItems(modId);
    }

    public static List<LibItemDefinition<?>> getItems(String modId) {
        List<LibItemDefinition<?>> items = ITEMS.get(modId);
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }

    protected static <T extends Item> LibItemDefinition<T> item(
            String modId, String englishName, String id, Function<Item.Properties, T> factory) {
        LibItemDefinition<T> definition = new LibItemDefinition<>(
                englishName,
                getRegister(modId).register(id, () -> factory.apply(new Item.Properties())));
        ITEMS.get(modId).add(definition);
        return definition;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends IPart> LibItemDefinition<PartItem<T>> part(
            String modId, String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        return item(modId, englishName, id, properties -> new PartItem<>(properties, (ItemStack stack) -> {
            IPartItem<T> partItem = (IPartItem<T>) stack.getItem();
            return factory.apply(partItem);
        }));
    }

    public void register(IEventBus eventBus) {
        getRegister(modId).register(eventBus);
    }
}
