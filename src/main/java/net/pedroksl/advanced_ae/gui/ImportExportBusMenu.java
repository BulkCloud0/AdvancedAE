package net.pedroksl.advanced_ae.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.ImportExportBusPart;

import appeng.container.implementations.IOBusContainer;

/**
 * 1.16.5 container for the import/export bus.
 */
public class ImportExportBusMenu extends IOBusContainer {

    public ImportExportBusMenu(int id, PlayerInventory playerInventory, ImportExportBusPart host) {
        this(AAEMenus.IMPORT_EXPORT_BUS.get(), id, playerInventory, host);
    }

    protected ImportExportBusMenu(
            ContainerType<?> containerType, int id, PlayerInventory playerInventory, ImportExportBusPart host) {
        super(containerType, id, playerInventory, host);
    }

    public ImportExportBusPart getHost() {
        return (ImportExportBusPart) this.getUpgradeable();
    }
}
