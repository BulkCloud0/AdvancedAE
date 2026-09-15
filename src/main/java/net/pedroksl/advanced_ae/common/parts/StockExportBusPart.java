package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.api.parts.IPartModel;
import appeng.container.ContainerLocator;
import appeng.container.ContainerOpener;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;

/**
 * 1.16.5 compatibility baseline for the stock export bus.
 *
 * <p>AE2 8.4.x does not expose the modern AEKey/GenericStack transfer strategy
 * API used by the newer implementation. Inherit the proven 8.4.x export-bus
 * behavior first while preserving this part's identity and custom menu. The
 * target-stock limiter is restored separately on top of the historical
 * IAEItemStack storage API.</p>
 */
public class StockExportBusPart extends ExportBusPart {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/stock_export_bus_part");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    public StockExportBusPart(ItemStack partItem) {
        super(partItem);
    }

    @Override
    public boolean onPartActivate(PlayerEntity player, Hand hand, Vector3d pos) {
        if (!isRemote()) {
            ContainerOpener.openContainer(
                    AAEMenus.STOCK_EXPORT_BUS.get(), player, ContainerLocator.forPart(this));
        }
        return true;
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
