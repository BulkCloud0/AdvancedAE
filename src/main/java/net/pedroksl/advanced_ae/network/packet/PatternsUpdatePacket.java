package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class PatternsUpdatePacket extends AddonPacket {
    private final List<Boolean> invalidPatterns;
    private final List<Boolean> enabledPatterns;

    public PatternsUpdatePacket(PacketBuffer stream) {
        List<Boolean> invalidList = new ArrayList<Boolean>();
        List<Boolean> enabledList = new ArrayList<Boolean>();

        int size = stream.readInt();
        for (int x = 0; x < size; x++) {
            invalidList.add(stream.readBoolean());
        }

        size = stream.readInt();
        for (int x = 0; x < size; x++) {
            enabledList.add(stream.readBoolean());
        }

        this.invalidPatterns = invalidList;
        this.enabledPatterns = enabledList;
    }

    public PatternsUpdatePacket(List<Boolean> invalidPatterns, List<Boolean> enabledPatterns) {
        this.invalidPatterns = invalidPatterns;
        this.enabledPatterns = enabledPatterns;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeInt(this.invalidPatterns.size());
        for (Boolean entry : this.invalidPatterns) {
            stream.writeBoolean(entry.booleanValue());
        }

        stream.writeInt(this.enabledPatterns.size());
        for (Boolean entry : this.enabledPatterns) {
            stream.writeBoolean(entry.booleanValue());
        }
    }

    @Override
    public void clientPacketData(PlayerEntity player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen) {
            QuantumCrafterScreen screen = (QuantumCrafterScreen) Minecraft.getInstance().screen;
            screen.updateInvalidButtons(this.invalidPatterns);
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }
}
