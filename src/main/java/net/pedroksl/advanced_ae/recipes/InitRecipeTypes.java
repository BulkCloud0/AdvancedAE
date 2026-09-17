package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import lombok.var;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.IForgeRegistry;

public class InitRecipeTypes {
    private static final class ToRegister {
        private final RecipeType<?> recipeType;
        private final ResourceLocation id;

        private ToRegister(RecipeType<?> recipeType, ResourceLocation id) {
            this.recipeType = recipeType;
            this.id = id;
        }
    }

    private static final List<ToRegister> toRegister = new ArrayList<>();

    public static <T extends Recipe<?>> RecipeType<T> register(String id) {
        RecipeType<T> type = RecipeType.simple(new ResourceLocation(id));
        toRegister.add(new ToRegister(type, new ResourceLocation(id)));
        return type;
    }

    public static void init(IForgeRegistry<RecipeType<?>> registry) {
        for (var entry : toRegister) {
            registry.register(entry.id, entry.recipeType);
        }
    }
}
