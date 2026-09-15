package net.pedroksl.advanced_ae.common.cluster;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;

public class AdvCraftingCPU implements ICraftingCPU {

    final UUID uniqueId;
    final long bytes;
    private final AdvCraftingCPUCluster cluster;
    public final AdvCraftingCPULogic craftingLogic = new AdvCraftingCPULogic(this);
    private final Map<IMEMonitorHandlerReceiver<IAEItemStack>, Object> listeners = new HashMap<>();
    private IAEItemStack finalOutput;

    public AdvCraftingCPU(final AdvCraftingCPUCluster cluster, final UUID uniqueId, final long bytes) {
        this.uniqueId = uniqueId;
        this.cluster = cluster;
        this.bytes = bytes;
    }

    protected AdvCraftingCPU(final AdvCraftingCPUCluster cluster, final long storage) {
        this.uniqueId = null;
        this.cluster = cluster;
        this.bytes = storage;
    }

    @Override
    public boolean isBusy() {
        return this.craftingLogic.hasJob();
    }

    @Override
    public IActionSource getActionSource() {
        return this.cluster.getSrc();
    }

    @Override
    public long getAvailableStorage() {
        return this.bytes;
    }

    @Override
    public int getCoProcessors() {
        return this.cluster.getCoProcessors();
    }

    @Nullable
    @Override
    public ITextComponent getName() {
        return this.cluster.getName();
    }

    @Override
    public void addListener(final IMEMonitorHandlerReceiver<IAEItemStack> listener, final Object verificationToken) {
        this.listeners.put(listener, verificationToken);
    }

    @Override
    public void removeListener(final IMEMonitorHandlerReceiver<IAEItemStack> listener) {
        this.listeners.remove(listener);
    }

    public void postChange(final IAEItemStack diff, final IActionSource source) {
        if (diff == null) {
            return;
        }

        final Iterator<Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> iterator =
                this.listeners.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final ImmutableList<IAEItemStack> changes = ImmutableList.of(diff.copy());
        while (iterator.hasNext()) {
            final Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object> entry = iterator.next();
            final IMEMonitorHandlerReceiver<IAEItemStack> receiver = entry.getKey();
            if (receiver.isValid(entry.getValue())) {
                receiver.postChange(null, changes, source);
            } else {
                iterator.remove();
            }
        }
    }

    public void cancelJob() {
        if (this.uniqueId == null) {
            return;
        }
        this.craftingLogic.cancel();
        this.cluster.cancelJob(this.uniqueId);
    }

    public AdvCpuSelectionMode getSelectionMode() {
        return this.cluster.getSelectionMode();
    }

    public void markDirty() {
        this.cluster.markDirty();
    }

    public boolean isActive() {
        return this.cluster.isActive();
    }

    @Nullable
    public World getLevel() {
        return this.cluster.getLevel();
    }

    @Nullable
    public IGrid getGrid() {
        return this.cluster.getGrid();
    }

    public void updateOutput(@Nullable final IAEItemStack stack) {
        this.finalOutput = stack == null ? null : stack.copy();
    }

    @Nullable
    public IAEItemStack getFinalOutput() {
        return this.finalOutput == null ? null : this.finalOutput.copy();
    }

    public void deactivate() {
        if (this.uniqueId != null) {
            this.cluster.deactivate(this.uniqueId);
        }
    }

    public IActionSource getSrc() {
        return this.cluster.getSrc();
    }

    public void writeToNBT(final CompoundNBT data) {
        this.craftingLogic.writeToNBT(data);
    }

    public void readFromNBT(final CompoundNBT data) {
        this.craftingLogic.readFromNBT(data);
    }
}
