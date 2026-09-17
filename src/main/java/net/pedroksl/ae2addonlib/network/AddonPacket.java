package net.pedroksl.ae2addonlib.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

/**
 * Forge 1.16.5 compatibility base for the packet contract used by AdvancedAE.
 */
public abstract class AddonPacket {

    protected abstract void write(PacketBuffer stream);

    public void serverPacketData(ServerPlayerEntity player) {
        // Optional per packet.
    }

    public void clientPacketData(PlayerEntity player) {
        // Optional per packet.
    }
}
