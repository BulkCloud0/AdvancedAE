package net.pedroksl.advanced_ae.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.container.AEBaseContainer;

/**
 * 1.16.5 compatibility container for per-pattern quantum-crafter settings.
 *
 * <p>The modern screen exchanges AEKey-based configuration maps and uses the
 * generic submenu framework. AE2 8.4.x predates both APIs. Keep the container
 * identity and host linkage here; stock/limit editing is restored when the
 * quantum crafter storage model is converted to IAEItemStack.</p>
 */
public class QuantumCrafterConfigPatternMenu extends AEBaseContainer {

    private final Object host;
    private int patternIndex = -1;

    public QuantumCrafterConfigPatternMenu(int id, PlayerInventory playerInventory, Object host) {
        this(AAEMenus.CRAFTER_PATTERN_CONFIG.get(), id, playerInventory, host);
    }

    public QuantumCrafterConfigPatternMenu(
            ContainerType<?> type, int id, PlayerInventory playerInventory, Object host) {
        super(type, id, playerInventory, host);
        this.host = host;
        createPlayerInventorySlots(playerInventory);
    }

    public Object getHost() {
        return host;
    }

    public int getPatternIndex() {
        return patternIndex;
    }

    public void setPatternIndex(int patternIndex) {
        this.patternIndex = patternIndex;
    }
}
