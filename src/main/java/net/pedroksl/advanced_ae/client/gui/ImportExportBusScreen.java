package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.ImportExportBusMenu;

import appeng.client.gui.implementations.IOBusScreen;
import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 import/export bus screen backed by AE2 8.4.x I/O controls.
 */
public class ImportExportBusScreen extends IOBusScreen<ImportExportBusMenu> {

    public ImportExportBusScreen(
            ImportExportBusMenu container,
            PlayerInventory playerInventory,
            ITextComponent title,
            ScreenStyle style) {
        super(container, playerInventory, title, style);
    }
}
