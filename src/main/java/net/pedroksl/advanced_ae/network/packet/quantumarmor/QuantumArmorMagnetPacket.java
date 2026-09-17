package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.GenericStack;

public class QuantumArmorMagnetPacket extends AddonPacket {

    private final int currentValue;
    private final List<GenericStack> filter;
    private final boolean blacklist;

    public QuantumArmorMagnetPacket(PacketBuffer stream) {
        currentValue = stream.readInt();

        int size = stream.readInt();
        List<GenericStack> list = new ArrayList<GenericStack>();
        for (int i = 0; i < size; i++) {
            list.add(GenericStack.readBuffer(stream));
        }
        filter = list;

        blacklist = stream.readBoolean();
    }

    public QuantumArmorMagnetPacket(int currentValue, List<GenericStack> filter, boolean blacklist) {
        this.currentValue = currentValue;
        this.filter = filter;
        this.blacklist = blacklist;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeInt(currentValue);

        stream.writeInt(filter.size());
        for (GenericStack genericStack : filter) {
            GenericStack.writeBuffer(genericStack, stream);
        }

        stream.writeBoolean(blacklist);
    }

    @Override
    public void serverPacketData(ServerPlayerEntity serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu) {
            QuantumArmorConfigMenu menu = (QuantumArmorConfigMenu) serverPlayer.containerMenu;
            menu.openMagnetScreen(currentValue, filter, blacklist);
        }
    }
}
