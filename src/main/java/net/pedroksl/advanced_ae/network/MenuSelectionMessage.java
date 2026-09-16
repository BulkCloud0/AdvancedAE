package net.pedroksl.advanced_ae.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/** Client-bound synchronization for AdvancedAE menu selection state. */
public final class MenuSelectionMessage {
    private final String key;
    private final int menuType;

    public MenuSelectionMessage(String key, int menuType) {
        this.key = key;
        this.menuType = menuType;
    }

    public static void encode(MenuSelectionMessage message, PacketBuffer buffer) {
        buffer.writeUtf(message.key);
        buffer.writeInt(message.menuType);
    }

    public static MenuSelectionMessage decode(PacketBuffer buffer) {
        return new MenuSelectionMessage(buffer.readUtf(), buffer.readInt());
    }

    public static void handle(MenuSelectionMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT, () -> () -> ClientAccess.handle(message)));
        context.setPacketHandled(true);
    }

    private static final class ClientAccess {
        private static void handle(MenuSelectionMessage message) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getPersistentData().putInt(message.key, message.menuType);
            }
        }
    }
}
