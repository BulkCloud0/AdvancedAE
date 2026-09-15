package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class InitRecipeTypes {

    private static final class ToRegister {
        private final IRecipeType<?> recipeType;
        private final ResourceLocation id;

        private ToRegister(IRecipeType<?> recipeType, ResourceLocation id) {
            this.recipeType = recipeType;
            this.id = id;
        }
    }

    private static final List<ToRegister> TO_REGISTER = new ArrayList<ToRegister>();

    private InitRecipeTypes() {
    }

    public static <T extends IRecipe<?>> IRecipeType<T> register(String id) {
        final ResourceLocation resourceLocation = new ResourceLocation(id);
        IRecipeType<T> type = new IRecipeType<T>() {
            @Override
            public String toString() {
                return resourceLocation.toString();
            }
        };
        TO_REGISTER.add(new ToRegister(type, resourceLocation));
        return type;
    }

    public static void init(IForgeRegistry<IRecipeType<?>> registry) {
        for (ToRegister entry : TO_REGISTER) {
            registry.register(entry.id, entry.recipeType);
        }
    }
}
