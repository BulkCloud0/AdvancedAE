package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class KeysPressedPacket extends AddonPacket {

    private final String data;
    private final boolean noKey;

    public KeysPressedPacket(PacketBuffer stream) {
        data = stream.readUtf();
        noKey = stream.readBoolean();
    }

    public KeysPressedPacket(String data, boolean noKey) {
        this.data = data;
        this.noKey = noKey;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeUtf(data);
        stream.writeBoolean(noKey);
    }

    @Override
    public void serverPacketData(ServerPlayerEntity player) {
        player.getPersistentData().putBoolean(data, noKey);
    }
}
