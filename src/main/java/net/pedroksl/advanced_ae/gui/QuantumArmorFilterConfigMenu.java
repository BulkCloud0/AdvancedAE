package net.pedroksl.advanced_ae.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.container.AEBaseContainer;

/**
 * 1.16.5 compatibility container for quantum-armor filter configuration.
 *
 * <p>The newer menu relies on GenericStack, ConfigInventory and the generic
 * submenu/amount-editor framework that were introduced after AE2 8.4.x. Keep
 * the menu identity and selection state here while the filter persistence is
 * moved to the historical item-stack storage API.</p>
 */
public class QuantumArmorFilterConfigMenu extends AEBaseContainer {

    public UpgradeType upgradeType = UpgradeType.EMPTY;
    public int slotIndex = -1;
    private final Object host;

    public QuantumArmorFilterConfigMenu(
            ContainerType<?> type, int id, PlayerInventory playerInventory, Object host) {
        super(type, id, playerInventory, host);
        this.host = host;
        createPlayerInventorySlots(playerInventory);
    }

    public QuantumArmorFilterConfigMenu(int id, PlayerInventory playerInventory, Object host) {
        this(AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG.get(), id, playerInventory, host);
    }

    public Object getHost() {
        return host;
    }

    public void setSlotIndex(int index) {
        this.slotIndex = index;
    }

    public void setUpgradeType(UpgradeType upgradeType) {
        this.upgradeType = upgradeType == null ? UpgradeType.EMPTY : upgradeType;
    }
}
