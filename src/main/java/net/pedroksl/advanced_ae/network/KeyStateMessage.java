package net.pedroksl.advanced_ae.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Server-bound key state update used by AdvancedAE hotkeys.
 * This is the Forge 1.16.5 replacement for the old addon-lib KeysPressedPacket.
 */
public final class KeyStateMessage {
    private final String key;
    private final boolean released;

    public KeyStateMessage(String key, boolean released) {
        this.key = key;
        this.released = released;
    }

    public static void encode(KeyStateMessage message, PacketBuffer buffer) {
        buffer.writeUtf(message.key);
        buffer.writeBoolean(message.released);
    }

    public static KeyStateMessage decode(PacketBuffer buffer) {
        return new KeyStateMessage(buffer.readUtf(128), buffer.readBoolean());
    }

    public static void handle(KeyStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayerEntity sender = context.getSender();
        if (sender != null) {
            context.enqueueWork(() -> sender.getPersistentData().putBoolean(message.key, message.released));
        }
        context.setPacketHandled(true);
    }

    public String key() {
        return key;
    }

    public boolean released() {
        return released;
    }
}
