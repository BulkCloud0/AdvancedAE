package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorUpgradeStatePacket extends AddonPacket {

    private final int selectedIndex;
    private final ItemStack stack;

    public QuantumArmorUpgradeStatePacket(PacketBuffer stream) {
        selectedIndex = stream.readInt();
        stack = stream.readItem();
    }

    public QuantumArmorUpgradeStatePacket(int selectedIndex, ItemStack stack) {
        this.selectedIndex = selectedIndex;
        this.stack = stack;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeInt(selectedIndex);
        stream.writeItem(stack);
    }

    @Override
    public void clientPacketData(PlayerEntity player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen) {
            QuantumArmorConfigScreen screen = (QuantumArmorConfigScreen) Minecraft.getInstance().screen;
            screen.refreshList(selectedIndex, stack);
        }
    }
}
