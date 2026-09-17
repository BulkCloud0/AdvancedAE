package net.pedroksl.advanced_ae.network.packet;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

/** Sends the content for a single quantum-crafter inventory to the terminal client. */
public class QuantumCrafterTerminalPacket extends AddonPacket {

    private final boolean fullUpdate;
    private final long inventoryId;
    private int inventorySize;
    private long sortBy;
    private final Int2ObjectMap<ItemStack> slots;
    private final Int2BooleanMap enabledArray;
    private final Int2BooleanMap invalidArray;

    public QuantumCrafterTerminalPacket(
            boolean fullUpdate,
            long inventoryId,
            int inventorySize,
            long sortBy,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanMap enabledArray,
            Int2BooleanMap invalidArray) {
        this.fullUpdate = fullUpdate;
        this.inventoryId = inventoryId;
        this.inventorySize = inventorySize;
        this.sortBy = sortBy;
        this.slots = slots;
        this.enabledArray = enabledArray;
        this.invalidArray = invalidArray;
    }

    public QuantumCrafterTerminalPacket(PacketBuffer stream) {
        this.inventoryId = stream.readVarLong();
        this.fullUpdate = stream.readBoolean();
        this.inventorySize = 0;
        this.sortBy = 0;
        if (fullUpdate) {
            this.inventorySize = stream.readVarInt();
            this.sortBy = stream.readVarLong();
        }

        int size = stream.readInt();
        this.slots = new Int2ObjectOpenHashMap<ItemStack>(size);
        for (int i = 0; i < size; i++) {
            int key = stream.readInt();
            ItemStack value = stream.readItem();
            this.slots.put(key, value);
        }

        size = stream.readInt();
        this.enabledArray = new Int2BooleanOpenHashMap(size);
        for (int i = 0; i < size; i++) {
            int key = stream.readInt();
            boolean value = stream.readBoolean();
            this.enabledArray.put(key, value);
        }

        size = stream.readInt();
        this.invalidArray = new Int2BooleanOpenHashMap(size);
        for (int i = 0; i < size; i++) {
            int key = stream.readInt();
            boolean value = stream.readBoolean();
            this.invalidArray.put(key, value);
        }
    }

    @Override
    public void write(PacketBuffer stream) {
        stream.writeVarLong(inventoryId);
        stream.writeBoolean(fullUpdate);
        if (fullUpdate) {
            stream.writeVarInt(inventorySize);
            stream.writeVarLong(sortBy);
        }

        stream.writeInt(slots.size());
        for (Int2ObjectMap.Entry<ItemStack> entry : slots.int2ObjectEntrySet()) {
            stream.writeInt(entry.getIntKey());
            stream.writeItem(entry.getValue());
        }

        stream.writeInt(enabledArray.size());
        for (Int2BooleanMap.Entry entry : enabledArray.int2BooleanEntrySet()) {
            stream.writeInt(entry.getIntKey());
            stream.writeBoolean(entry.getBooleanValue());
        }

        stream.writeInt(invalidArray.size());
        for (Int2BooleanMap.Entry entry : invalidArray.int2BooleanEntrySet()) {
            stream.writeInt(entry.getIntKey());
            stream.writeBoolean(entry.getBooleanValue());
        }
    }

    public static QuantumCrafterTerminalPacket fullUpdate(
            long inventoryId,
            int inventorySize,
            long sortBy,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanArrayMap enabledArray,
            Int2BooleanArrayMap invalidArray) {
        return new QuantumCrafterTerminalPacket(
                true, inventoryId, inventorySize, sortBy, slots, enabledArray, invalidArray);
    }

    public static QuantumCrafterTerminalPacket incrementalUpdate(
            long inventoryId,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanArrayMap enabledArray,
            Int2BooleanArrayMap invalidArray) {
        return new QuantumCrafterTerminalPacket(false, inventoryId, 0, 0, slots, enabledArray, invalidArray);
    }

    @Override
    public void clientPacketData(PlayerEntity player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen) {
            QuantumCrafterTermScreen<?> screen = (QuantumCrafterTermScreen<?>) Minecraft.getInstance().screen;
            if (fullUpdate) {
                screen.postFullUpdate(this.inventoryId, sortBy, inventorySize, slots, enabledArray, invalidArray);
            } else {
                screen.postIncrementalUpdate(this.inventoryId, slots, enabledArray, invalidArray);
            }
        }
    }
}
