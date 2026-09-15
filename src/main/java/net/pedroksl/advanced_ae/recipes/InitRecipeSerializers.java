package net.pedroksl.advanced_ae.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class InitRecipeSerializers {

    private InitRecipeSerializers() {
    }

    public static void init(IForgeRegistry<IRecipeSerializer<?>> registry) {
        register(registry, ReactionChamberRecipe.TYPE_ID, ReactionChamberRecipeSerializer.INSTANCE);
    }

    private static void register(
            IForgeRegistry<IRecipeSerializer<?>> registry,
            ResourceLocation id,
            IRecipeSerializer<?> serializer) {
        registry.register(id, serializer);
    }
}
