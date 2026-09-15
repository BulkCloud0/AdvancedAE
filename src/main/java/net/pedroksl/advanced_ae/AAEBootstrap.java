/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 */
package net.pedroksl.advanced_ae;

import net.minecraftforge.fml.common.Mod;

@Mod(AdvancedAE.MOD_ID)
public class AAEBootstrap {
    public AAEBootstrap() {
        // Client initialization is temporarily isolated from the 1.16.5 compile
        // baseline. Re-enable AAEClient once its Forge 1.16 event hooks are ported.
        new AdvancedAE();
    }
}
