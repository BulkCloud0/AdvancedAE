package net.pedroksl.advanced_ae.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

/** Forge/Minecraft 1.16.5 recipe-type registration compatibility helper. */
public final class InitRecipeTypes {
    private InitRecipeTypes() {}

    public static <T extends IRecipe<?>> IRecipeType<T> register(String id) {
        return IRecipeType.register(id);
    }
}
