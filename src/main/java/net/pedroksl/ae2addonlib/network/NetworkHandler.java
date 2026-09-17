package net.pedroksl.ae2addonlib.network;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Forge 1.16.5 implementation of the lightweight packet API used by the
 * original AdvancedAE sources.
 */
public class NetworkHandler {

    private static final String PROTOCOL = "1";

    private final SimpleChannel channel;
    private int nextPacketId;

    protected NetworkHandler(String modId) {
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(modId, "main"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals);
    }

    protected <T extends AddonPacket> void registerPacket(Class<T> packetClass, Function<PacketBuffer, T> decoder) {
        final int packetId = this.nextPacketId++;
        this.channel.registerMessage(
                packetId,
                packetClass,
                AddonPacket::write,
                decoder,
                NetworkHandler::handlePacket);
    }

    private static <T extends AddonPacket> void handlePacket(T packet, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                ServerPlayerEntity sender = context.getSender();
                if (sender != null) {
                    packet.serverPacketData(sender);
                }
            } else {
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                    if (Minecraft.getInstance().player != null) {
                        packet.clientPacketData(Minecraft.getInstance().player);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }

    public void sendToServer(AddonPacket packet) {
        this.channel.sendToServer(packet);
    }

    public void sendToPlayer(ServerPlayerEntity player, AddonPacket packet) {
        this.channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public void sendToPlayer(AddonPacket packet, ServerPlayerEntity player) {
        this.sendToPlayer(player, packet);
    }

    public void sendToTracking(Entity entity, AddonPacket packet) {
        this.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }

    public void sendToTrackingAndSelf(Entity entity, AddonPacket packet) {
        this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
    }

    public void sendToAll(AddonPacket packet) {
        this.channel.send(PacketDistributor.ALL.noArg(), packet);
    }

    public SimpleChannel getChannel() {
        return this.channel;
    }
}
