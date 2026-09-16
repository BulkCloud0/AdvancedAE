package dev.bulkcloud.expansionae;

import com.glodblock.github.extendedae.ExtendedAE;

import net.minecraftforge.fml.common.Mod;
import net.pedroksl.advanced_ae.AdvancedAE;

/**
 * Single Forge mod container for the unified AdvancedAE + ExtendedAE 1.16.5 port.
 *
 * <p>The original content namespaces remain unchanged while the backport is in
 * progress. This keeps existing recipes, models, tags and registry identifiers
 * stable, but Forge only discovers one mod: {@code expansionae}.
 */
@Mod(ExpansionAE.MOD_ID)
public final class ExpansionAE {

    public static final String MOD_ID = "expansionae";

    public ExpansionAE() {
        ExtendedAE.bootstrap();
        new AdvancedAE();
    }
}
