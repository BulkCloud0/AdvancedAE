package net.pedroksl.advanced_ae.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;

public final class InitRecipeSerializers {
    private static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AdvancedAE.MOD_ID);

    static {
        SERIALIZERS.register("reaction", () -> ReactionChamberRecipeSerializer.INSTANCE);
    }

    private InitRecipeSerializers() {}

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
