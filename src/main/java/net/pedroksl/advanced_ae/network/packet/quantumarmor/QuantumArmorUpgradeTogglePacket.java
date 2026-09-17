package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorUpgradeTogglePacket extends AddonPacket {

    private final UpgradeType upgradeType;
    private final boolean state;

    public QuantumArmorUpgradeTogglePacket(PacketBuffer stream) {
        upgradeType = stream.readEnum(UpgradeType.class);
        state = stream.readBoolean();
    }

    public QuantumArmorUpgradeTogglePacket(UpgradeType upgradeType, boolean state) {
        this.upgradeType = upgradeType;
        this.state = state;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeEnum(upgradeType);
        stream.writeBoolean(state);
    }

    @Override
    public void serverPacketData(ServerPlayerEntity serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu) {
            QuantumArmorConfigMenu menu = (QuantumArmorConfigMenu) serverPlayer.containerMenu;
            menu.toggleUpgradeEnable(upgradeType, state);
        }
    }
}
