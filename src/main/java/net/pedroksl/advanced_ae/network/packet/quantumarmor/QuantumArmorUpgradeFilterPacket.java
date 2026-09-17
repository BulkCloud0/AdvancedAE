package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.GenericStack;

public class QuantumArmorUpgradeFilterPacket extends AddonPacket {

    private final UpgradeType upgradeType;
    private final List<GenericStack> filter;

    public QuantumArmorUpgradeFilterPacket(PacketBuffer stream) {
        upgradeType = stream.readEnum(UpgradeType.class);

        int size = stream.readInt();
        List<GenericStack> list = new ArrayList<GenericStack>();
        for (int i = 0; i < size; i++) {
            list.add(GenericStack.readBuffer(stream));
        }
        filter = list;
    }

    public QuantumArmorUpgradeFilterPacket(UpgradeType upgradeType, List<GenericStack> filter) {
        this.upgradeType = upgradeType;
        this.filter = filter;
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeEnum(upgradeType);

        stream.writeInt(filter.size());
        for (GenericStack genericStack : filter) {
            GenericStack.writeBuffer(genericStack, stream);
        }
    }

    @Override
    public void serverPacketData(ServerPlayerEntity serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu) {
            QuantumArmorConfigMenu menu = (QuantumArmorConfigMenu) serverPlayer.containerMenu;
            menu.openFilterConfigScreen(upgradeType, filter);
        }
    }
}
