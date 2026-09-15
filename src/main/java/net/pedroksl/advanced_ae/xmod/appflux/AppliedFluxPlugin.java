package net.pedroksl.advanced_ae.xmod.appflux;

import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.Upgrades;

public class AppliedFluxPlugin {

    public static void init() {
        try {
            Upgrades.add(
                    AFItemAndBlock.INDUCTION_CARD,
                    AAEBlocks.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFItemAndBlock.INDUCTION_CARD,
                    AAEItems.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFItemAndBlock.INDUCTION_CARD,
                    AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFItemAndBlock.INDUCTION_CARD,
                    AAEItems.SMALL_ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

    public static double rechargeAeStorageItem(
            IGrid grid, double neededPower, Player player, ItemStack stack, IAEItemPowerStorage aePowerStorage) {
        try {
            double neededFePower = PowerMultiplier.CONFIG.divide(neededPower);

            double extracted = PowerMultiplier.CONFIG.multiply(grid.getStorageService()
                    .getInventory()
                    .extract(
                            FluxKey.of(EnergyType.FE),
                            (long) neededFePower,
                            Actionable.MODULATE,
                            IActionSource.ofPlayer(player)));

            double remainder = aePowerStorage.injectAEPower(stack, extracted, Actionable.MODULATE);
            grid.getStorageService()
                    .getInventory()
                    .insert(
                            FluxKey.of(EnergyType.FE),
                            (long) PowerMultiplier.CONFIG.divide(remainder),
                            Actionable.MODULATE,
                            IActionSource.ofPlayer(player));

            neededPower -= extracted - remainder;
        } catch (Throwable ignored) {
            // NO_OP
        }
        return neededPower;
    }

    public static void rechargeEnergyStorage(IGrid grid, int afRate, IActionSource source, IEnergyStorage cap) {
        try {
            long extracted = grid.getStorageService()
                    .getInventory()
                    .extract(FluxKey.of(EnergyType.FE), afRate, Actionable.MODULATE, source);
            int inserted = cap.receiveEnergy((int) extracted, false);
            grid.getStorageService()
                    .getInventory()
                    .insert(FluxKey.of(EnergyType.FE), extracted - inserted, Actionable.MODULATE, source);
        } catch (Throwable ignored) {
            // NO_OP
        }
    }

    public static void notifyBlockUpdate(Object obj, BlockPos self, BlockPos from) {
        try {
            if (obj instanceof INeighborListener) {
                INeighborListener listener = (INeighborListener) obj;
                AFUtil.notifyNeighbor(listener, self, from);
            }
        } catch (Throwable ignored) {
            // NO_OP
        }
    }

    public static void notifyBlockUpdate(Object obj, Direction face, BlockPos self, BlockPos from) {
        try {
            Direction d = AFUtil.getBlockDirection(self, from);
            if (d == face && d != null && obj instanceof INeighborListener) {
                INeighborListener listener = (INeighborListener) obj;
                listener.onChange(d);
            }
        } catch (Throwable ignored) {
            // NO_OP
        }
    }
}
