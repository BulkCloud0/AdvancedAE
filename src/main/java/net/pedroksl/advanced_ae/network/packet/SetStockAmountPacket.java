package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class SetStockAmountPacket extends AddonPacket {
    private final int index;
    private final long amount;

    public SetStockAmountPacket(PacketBuffer stream) {
        index = stream.readInt();
        amount = stream.readLong();
    }

    public SetStockAmountPacket(int index, long amount) {
        this.index = index;
        this.amount = amount;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeInt(this.index);
        stream.writeLong(this.amount);
    }

    @Override
    public void serverPacketData(ServerPlayerEntity player) {
        if (player.containerMenu instanceof QuantumCrafterConfigPatternMenu) {
            QuantumCrafterConfigPatternMenu menu = (QuantumCrafterConfigPatternMenu) player.containerMenu;
            menu.setStockAmount(this.index, this.amount);
        }
    }
}
