package net.pedroksl.advanced_ae.xmod;

import net.minecraftforge.fml.ModList;

/**
 * Lightweight 1.16.5 replacement for the AE2AddonLib addon descriptor.
 * Keeps optional integrations lazy so missing companion mods never prevent startup.
 */
public enum Addons {
    EXPATTERNPROVIDER("expatternprovider", "Extended AE"),
    APPMEK("appmek", "Applied Mekanistics"),
    AE2WTLIB("ae2wtlib", "AE2 Wireless Terminals Lib"),
    APPFLUX("appflux", "Applied Flux"),
    MEGACELLS("megacells", "MEGACells"),
    MEKANISM("mekanism", "Mekanism"),
    IRIS("oculus", "Oculus"),
    CURIOS("curios", "Curios"),
    APOTHIC_ENCHANTING("apotheosis", "Apotheosis");

    private final String modId;
    private final String modName;

    Addons(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
    }

    public String getModId() {
        return this.modId;
    }

    public String getModName() {
        return this.modName;
    }

    public boolean isLoaded() {
        return ModList.get().isLoaded(this.modId);
    }
}
