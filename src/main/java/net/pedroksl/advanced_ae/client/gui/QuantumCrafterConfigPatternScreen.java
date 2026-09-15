package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

/**
 * 1.16.5 compatibility screen for quantum-crafter pattern configuration.
 */
public class QuantumCrafterConfigPatternScreen extends AEBaseScreen<QuantumCrafterConfigPatternMenu> {

    public QuantumCrafterConfigPatternScreen(
            QuantumCrafterConfigPatternMenu menu,
            PlayerInventory playerInventory,
            ITextComponent title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
        new AESubScreen(menu.getHost()).addBackButton("back", widgets);
    }
}
