package net.pedroksl.advanced_ae.network.packet;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class ItemTrackingPacket extends AddonPacket {

    private final UUID thrower;
    private final int entityId;
    private final int pickupDelay;

    public ItemTrackingPacket(PacketBuffer stream) {
        this.thrower = stream.readUUID();
        this.entityId = stream.readInt();
        this.pickupDelay = stream.readInt();
    }

    public ItemTrackingPacket(ItemEntity item) {
        this.thrower = item.getThrower();
        this.entityId = item.getId();
        this.pickupDelay = item.hasPickUpDelay() ? 10 : 0;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeUUID(this.thrower);
        stream.writeInt(this.entityId);
        stream.writeInt(this.pickupDelay);
    }

    @Override
    public void clientPacketData(PlayerEntity player) {
        if (player != null && player.level != null) {
            Entity entity = player.level.getEntity(this.entityId);
            if (entity instanceof ItemEntity) {
                ItemEntity item = (ItemEntity) entity;
                item.setThrower(this.thrower);
                item.setPickUpDelay(this.pickupDelay);
            }
        }
    }
}
