package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.pedroksl.advanced_ae.gui.QuantumArmorFilterConfigMenu;

import appeng.client.gui.style.ScreenStyle;

public class QuantumArmorFilterConfigScreen extends QuantumArmorFilterScreen<QuantumArmorFilterConfigMenu> {

    public QuantumArmorFilterConfigScreen(
            QuantumArmorFilterConfigMenu menu,
            PlayerInventory playerInventory,
            ITextComponent title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
