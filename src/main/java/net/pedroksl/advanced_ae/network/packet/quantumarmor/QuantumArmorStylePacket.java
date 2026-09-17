package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.gui.QuantumArmorStyleConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorStylePacket extends AddonPacket {

    private final List<Integer> slots;
    private final int color;

    public QuantumArmorStylePacket(PacketBuffer stream) {
        int size = stream.readInt();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            list.add(stream.readInt());
        }
        slots = list;
        color = stream.readInt();
    }

    public QuantumArmorStylePacket(List<Integer> slots, int color) {
        this.slots = slots;
        this.color = color;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeInt(slots.size());
        for (Integer slotIndex : slots) {
            stream.writeInt(slotIndex.intValue());
        }
        stream.writeInt(color);
    }

    @Override
    public void serverPacketData(ServerPlayerEntity serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorStyleConfigMenu) {
            QuantumArmorStyleConfigMenu menu = (QuantumArmorStyleConfigMenu) serverPlayer.containerMenu;
            menu.updateItemColors(this.slots, this.color);
        }
    }
}
