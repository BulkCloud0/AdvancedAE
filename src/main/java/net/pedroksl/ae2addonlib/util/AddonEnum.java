package net.pedroksl.ae2addonlib.util;

import net.minecraftforge.fml.ModList;

/** Minimal compatibility surface used by AdvancedAE integrations on Forge 1.16.5. */
public interface AddonEnum {
    String getModId();

    String getModName();

    default boolean isLoaded() {
        return ModList.get().isLoaded(getModId());
    }
}
