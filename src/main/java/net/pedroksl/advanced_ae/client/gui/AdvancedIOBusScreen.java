package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;

import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 advanced I/O bus screen.
 */
public class AdvancedIOBusScreen extends StockExportBusScreen {

    public AdvancedIOBusScreen(
            AdvancedIOBusMenu menu, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
