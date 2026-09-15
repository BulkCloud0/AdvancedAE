package net.pedroksl.ae2addonlib.util;

import net.minecraftforge.fml.ModList;

/**
 * Minimal 1.16.5 compatibility contract for optional AdvancedAE integrations.
 */
public interface AddonEnum {
    String getModId();

    String getModName();

    default boolean isLoaded() {
        return ModList.get().isLoaded(getModId());
    }
}
