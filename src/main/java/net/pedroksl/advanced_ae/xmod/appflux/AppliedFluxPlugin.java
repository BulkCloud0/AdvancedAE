package net.pedroksl.advanced_ae.xmod.appflux;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;

import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;

/**
 * Compatibility hook for Applied Flux. Applied Flux has no supported 1.16.5 dependency in
 * this backport, so these hooks intentionally degrade to AE-only behaviour without linking
 * unavailable classes at class-load time.
 */
public final class AppliedFluxPlugin {
    private AppliedFluxPlugin() {}

    public static void init() {
        // No 1.16.5 Applied Flux implementation is available to register against.
    }

    public static double rechargeAeStorageItem(
            IGrid grid, double neededPower, PlayerEntity player, ItemStack stack, IAEItemPowerStorage aePowerStorage) {
        return neededPower;
    }

    public static void rechargeEnergyStorage(IGrid grid, int afRate, IActionSource source, IEnergyStorage cap) {
        // No-op when Applied Flux is absent.
    }

    public static void notifyBlockUpdate(Object obj, BlockPos self, BlockPos from) {
        // No-op when Applied Flux is absent.
    }

    public static void notifyBlockUpdate(Object obj, Direction face, BlockPos self, BlockPos from) {
        // No-op when Applied Flux is absent.
    }
}
