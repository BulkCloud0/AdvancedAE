package net.pedroksl.advanced_ae.xmod;

import net.minecraftforge.fml.ModList;
import net.pedroksl.ae2addonlib.integration.AddonEnum;

/**
 * Lightweight 1.16.5 replacement for the AE2AddonLib addon descriptor.
 * Keeps optional integrations lazy so missing companion mods never prevent startup.
 */
public enum Addons implements AddonEnum {
    EXPATTERNPROVIDER("expatternprovider", "Extended AE"),
    APPMEK("appmek", "Applied Mekanistics"),
    AE2WTLIB("ae2wtlib", "AE2 Wireless Terminals Lib"),
    APPFLUX("appflux", "Applied Flux"),
    MEGACELLS("megacells", "MEGACells"),
    MEKANISM("mekanism", "Mekanism"),
    IRIS("oculus", "Oculus"),
    CURIOS("curios", "Curios"),
    APOTHIC_ENCHANTING("apotheosis", "Apotheosis");

    private static final String EXPANSION_AE_MOD_ID = "expansionae";

    private final String modId;
    private final String modName;

    Addons(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
    }

    @Override
    public String getModId() {
        return this.modId;
    }

    @Override
    public String getModName() {
        return this.modName;
    }

    @Override
    public boolean isLoaded() {
        if (this == EXPATTERNPROVIDER && ModList.get().isLoaded(EXPANSION_AE_MOD_ID)) {
            return true;
        }
        return ModList.get().isLoaded(this.modId);
    }
}
