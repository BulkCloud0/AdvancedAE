package net.pedroksl.advanced_ae.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

/**
 * 1.16.5 advanced I/O bus container.
 *
 * <p>The generic addon setting synchronization used by newer AE2 versions is
 * not present in AE2 8.4.x. Standard I/O bus behavior is inherited from
 * {@link StockExportBusMenu}; custom stock regulation will be restored when the
 * part itself is ported to the 8.4.x storage API.</p>
 */
public class AdvancedIOBusMenu extends StockExportBusMenu {

    public AdvancedIOBusMenu(int id, PlayerInventory playerInventory, StockExportBusPart host) {
        super(AAEMenus.ADVANCED_IO_BUS.get(), id, playerInventory, host);
    }
}
