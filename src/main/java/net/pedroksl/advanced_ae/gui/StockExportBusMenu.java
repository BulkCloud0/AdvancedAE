package net.pedroksl.advanced_ae.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

import appeng.container.implementations.IOBusContainer;

/**
 * AE2 8.4.x container for the stock export bus.
 *
 * <p>The newer menu used GenericStack-based amount editing and the addon-lib
 * submenu framework. Neither API exists on 1.16.5. The native IOBusContainer
 * already supplies the configuration, upgrade and scheduling slots required by
 * an ExportBusPart, so it is the correct compatibility baseline.</p>
 */
public class StockExportBusMenu extends IOBusContainer {

    public StockExportBusMenu(int id, PlayerInventory playerInventory, StockExportBusPart host) {
        this(AAEMenus.STOCK_EXPORT_BUS.get(), id, playerInventory, host);
    }

    protected StockExportBusMenu(
            ContainerType<?> containerType, int id, PlayerInventory playerInventory, StockExportBusPart host) {
        super(containerType, id, playerInventory, host);
    }

    public StockExportBusPart getHost() {
        return (StockExportBusPart) this.getUpgradeable();
    }
}
