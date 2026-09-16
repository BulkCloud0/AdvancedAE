package net.pedroksl.advanced_ae.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.pedroksl.advanced_ae.AdvancedAE;

/** Forge 1.16.5-native transport for AdvancedAE packets. */
public final class AAENetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

    private final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            AdvancedAE.makeId("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private int nextMessageId;
    private boolean initialized;

    private AAENetworkHandler() {}

    public synchronized void init() {
        if (initialized) {
            return;
        }

        register(
                KeyStateMessage.class,
                KeyStateMessage::encode,
                KeyStateMessage::decode,
                KeyStateMessage::handle,
                NetworkDirection.PLAY_TO_SERVER);
        register(
                MenuSelectionMessage.class,
                MenuSelectionMessage::encode,
                MenuSelectionMessage::decode,
                MenuSelectionMessage::handle,
                NetworkDirection.PLAY_TO_CLIENT);
        initialized = true;
    }

    public synchronized <MSG> void register(
            Class<MSG> messageType,
            BiConsumer<MSG, PacketBuffer> encoder,
            Function<PacketBuffer, MSG> decoder,
            BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer,
            NetworkDirection direction) {
        channel.messageBuilder(messageType, nextMessageId++, direction)
                .encoder(encoder)
                .decoder(decoder)
                .consumer(consumer)
                .add();
    }

    public <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }

    public <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public SimpleChannel channel() {
        return channel;
    }
}
