package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.AEKey;

public class AdvPatternEncoderChangeDirectionPacket extends AddonPacket {

    private final AEKey key;
    private final Direction dir;

    public AdvPatternEncoderChangeDirectionPacket(PacketBuffer stream) {
        this.key = AEKey.readKey(stream);
        this.dir = stream.readBoolean() ? stream.readEnum(Direction.class) : null;
    }

    public AdvPatternEncoderChangeDirectionPacket(AEKey key, @Nullable Direction dir) {
        this.key = key;
        this.dir = dir;
    }

    @Override
    public void write(PacketBuffer stream) {
        writeKey(stream, this.key);
        if (this.dir == null) {
            stream.writeBoolean(false);
        } else {
            stream.writeBoolean(true);
            stream.writeEnum(this.dir);
        }
    }

    @Override
    public void serverPacketData(ServerPlayerEntity player) {
        if (player.containerMenu instanceof AdvPatternEncoderMenu) {
            AdvPatternEncoderMenu encoderContainer = (AdvPatternEncoderMenu) player.containerMenu;
            encoderContainer.update(this.key, this.dir);
        }
    }
}
