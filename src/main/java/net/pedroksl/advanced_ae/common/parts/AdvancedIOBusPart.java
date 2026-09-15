package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;

/**
 * 1.16.5 compatibility baseline for the advanced I/O bus.
 *
 * <p>The modern bidirectional transfer implementation relies on AE2 transfer
 * strategies introduced long after 8.4.x. Until that logic is expressed using
 * the historical storage channel APIs, retain the stock export-bus behavior
 * and the advanced bus' own model identity.</p>
 */
public class AdvancedIOBusPart extends StockExportBusPart {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/advanced_io_bus_part");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    public AdvancedIOBusPart(ItemStack partItem) {
        super(partItem);
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
