package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.StockExportBusMenu;

import appeng.client.gui.implementations.IOBusScreen;
import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 stock export-bus screen.
 *
 * <p>The modern implementation duplicated the standard I/O bus controls and
 * relied on newer GuiGraphics/GenericStack APIs. AE2 8.4.x already provides
 * those controls through {@link IOBusScreen}, so use the native screen as the
 * compatibility baseline. The custom numeric stock editor can be layered back
 * on once the part inventory has been ported to AE2 8.4.x.</p>
 */
public class StockExportBusScreen<M extends StockExportBusMenu> extends IOBusScreen<M> {

    public StockExportBusScreen(
            M container, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(container, playerInventory, title, style);
    }
}
