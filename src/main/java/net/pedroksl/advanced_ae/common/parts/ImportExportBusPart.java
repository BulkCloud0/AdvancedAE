package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;

/**
 * 1.16.5 compatibility baseline for the combined import/export bus.
 *
 * <p>The modern import strategy API does not exist in AE2 8.4.x. Export
 * behavior is inherited from the historical ExportBusPart while the import
 * half and custom menu are restored in a later storage-API pass.</p>
 */
public class ImportExportBusPart extends ExportBusPart {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/import_export_bus_part");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(
            MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    public ImportExportBusPart(ItemStack partItem) {
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
