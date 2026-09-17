package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class ClearQuantumCrafterTerminalPacket extends AddonPacket {

    public ClearQuantumCrafterTerminalPacket(PacketBuffer stream) {}

    public ClearQuantumCrafterTerminalPacket() {}

    @Override
    public void write(PacketBuffer data) {}

    @Override
    public void clientPacketData(PlayerEntity player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen) {
            QuantumCrafterTermScreen<?> screen = (QuantumCrafterTermScreen<?>) Minecraft.getInstance().screen;
            screen.clear();
        }
    }
}
