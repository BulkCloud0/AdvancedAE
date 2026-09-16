package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.AEKey;

public class AdvPatternEncoderPacket extends AddonPacket {

    private final LinkedHashMap<AEKey, Direction> dirMap;

    public AdvPatternEncoderPacket(FriendlyByteBuf stream) {
        this.dirMap = new LinkedHashMap<AEKey, Direction>();

        int size = stream.readInt();
        for (int x = 0; x < size; x++) {
            AEKey key = AEKey.readKey(stream);
            Direction dir = stream.readBoolean() ? stream.readEnum(Direction.class) : null;
            dirMap.put(key, dir);
        }
    }

    public AdvPatternEncoderPacket(LinkedHashMap<AEKey, Direction> dirMap) {
        this.dirMap = dirMap;
    }

    public AdvPatternEncoderPacket() {
        this.dirMap = new LinkedHashMap<AEKey, Direction>();
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeInt(dirMap.size());
        for (Map.Entry<AEKey, Direction> entry : dirMap.entrySet()) {
            writeKey(stream, entry.getKey());
            Direction dir = entry.getValue();
            if (dir == null) {
                stream.writeBoolean(false);
            } else {
                stream.writeBoolean(true);
                stream.writeEnum(dir);
            }
        }
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderScreen) {
            AdvPatternEncoderScreen encoderGui = (AdvPatternEncoderScreen) Minecraft.getInstance().screen;
            encoderGui.update(this.dirMap);
        }
    }
}
