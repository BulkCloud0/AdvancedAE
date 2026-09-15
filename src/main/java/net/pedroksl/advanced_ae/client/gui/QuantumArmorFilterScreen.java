package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.QuantumArmorFilterConfigMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 armor filter screen.
 *
 * <p>The modern amount-editing tooltip depends on GenericStack and GuiGraphics,
 * neither of which exists in AE2 8.4.x / Minecraft 1.16.5. Keep the filter
 * screen and its return navigation on the historical AE2 GUI API; quantity
 * editing will be restored together with the filter container storage model.</p>
 */
public class QuantumArmorFilterScreen<M extends QuantumArmorFilterConfigMenu> extends AEBaseScreen<M> {

    public QuantumArmorFilterScreen(
            M menu, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        new AESubScreen(menu.getHost()).addBackButton("back", widgets);
    }
}
