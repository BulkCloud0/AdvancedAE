package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;

import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 advanced I/O bus screen.
 *
 * <p>Custom addon settings used by the newer screen depend on the modern
 * generic setting API. The AE2 8.4.x I/O controls are provided by the parent
 * screen and form the compatibility baseline for the port.</p>
 */
public class AdvancedIOBusScreen extends StockExportBusScreen<AdvancedIOBusMenu> {

    public AdvancedIOBusScreen(
            AdvancedIOBusMenu menu, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
