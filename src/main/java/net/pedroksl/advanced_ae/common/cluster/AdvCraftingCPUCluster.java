package net.pedroksl.advanced_ae.common.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.networking.security.IActionSource;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.me.helpers.MachineSource;

public class AdvCraftingCPUCluster implements IAECluster {

    private final BlockPos boundsMin;
    private final BlockPos boundsMax;

    private final Map<UUID, AdvCraftingCPU> activeCpus = new HashMap<>();
    private AdvCraftingCPU remainingStorageCpu;
    private final List<AdvCraftingBlockEntity> blockEntities = new ArrayList<>();
    private ITextComponent myName;
    private boolean isDestroyed;
    private long storage;
    private long storageMultiplier;
    private long remainingStorage;
    private MachineSource machineSrc;
    private int accelerator;
    private int acceleratorMultiplier;
    private AdvCpuSelectionMode selectionMode = AdvCpuSelectionMode.ANY;

    public AdvCraftingCPUCluster(final BlockPos boundsMin, final BlockPos boundsMax) {
        this.boundsMin = boundsMin.toImmutable();
        this.boundsMax = boundsMax.toImmutable();
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    @Override
    public BlockPos getBoundsMin() {
        return this.boundsMin;
    }

    @Override
    public BlockPos getBoundsMax() {
        return this.boundsMax;
    }

    @Override
    public void updateStatus(final boolean updateGrid) {
        for (final AdvCraftingBlockEntity tile : this.blockEntities) {
            tile.updateSubType(true);
        }
    }

    @Override
    public void destroy() {
        if (this.isDestroyed) {
            return;
        }
        this.isDestroyed = true;

        final boolean ownsModification = !MBCalculator.isModificationInProgress();
        if (ownsModification) {
            MBCalculator.setModificationInProgress(this);
        }
        try {
            updateGridForChangedCpu(null);
        } finally {
            if (ownsModification) {
                MBCalculator.setModificationInProgress(null);
            }
        }
    }

    private void updateGridForChangedCpu(@Nullable final AdvCraftingCPUCluster cluster) {
        boolean posted = false;
        for (final AdvCraftingBlockEntity tile : this.blockEntities) {
            final IGridNode node = tile.getActionableNode();
            if (node != null && !posted) {
                final IGrid grid = node.getGrid();
                if (grid != null) {
                    grid.postEvent(new MENetworkCraftingCpuChange(node));
                    posted = true;
                }
            }
            tile.updateStatus(cluster);
        }
    }

    @Override
    public Iterator<AdvCraftingBlockEntity> getTiles() {
        return this.blockEntities.iterator();
    }

    /**
     * Kept as an addon-facing alias for the multiblock calculator and existing
     * callers while IAECluster itself uses getTiles() in AE2 8.4.7.
     */
    public Iterator<AdvCraftingBlockEntity> getBlockEntities() {
        return getTiles();
    }

    public int numBlockEntities() {
        return this.blockEntities.size();
    }

    void addBlockEntity(final AdvCraftingBlockEntity tile) {
        if (this.machineSrc == null || tile.isCoreBlock()) {
            this.machineSrc = new MachineSource(tile);
        }

        tile.setCoreBlock(false);
        tile.saveChanges();
        this.blockEntities.add(0, tile);

        if (tile.getStorageBytes() > 0) {
            this.storage += tile.getStorageBytes();
            recalculateRemainingStorage();
        }
        if (tile.getStorageMultiplier() > 0) {
            this.storageMultiplier += tile.getStorageMultiplier();
            recalculateRemainingStorage();
        }
        if (tile.getAcceleratorThreads() > 0) {
            if (tile.getAcceleratorThreads() > 16) {
                throw new IllegalArgumentException("Co-processor threads may not exceed 16 per single unit block.");
            }
            this.accelerator += tile.getAcceleratorThreads();
        }
        if (tile.getAccelerationMultiplier() > 0) {
            this.acceleratorMultiplier += tile.getAccelerationMultiplier();
        }
    }

    public void recalculateRemainingStorage() {
        long totalStorage = this.storage;
        if (this.storageMultiplier > 0) {
            totalStorage *= this.storageMultiplier;
        }

        long usedStorage = 0;
        for (final AdvCraftingCPU cpu : this.activeCpus.values()) {
            usedStorage += cpu.getAvailableStorage();
        }

        this.remainingStorage = Math.max(0, totalStorage - usedStorage);
        this.remainingStorageCpu = null;
    }

    public void markDirty() {
        final AdvCraftingBlockEntity core = this.getCore();
        if (core != null) {
            core.saveChanges();
        }
    }

    public IActionSource getSrc() {
        return Objects.requireNonNull(this.machineSrc, "Crafting cluster is not initialized");
    }

    @Nullable
    private AdvCraftingBlockEntity getCore() {
        if (this.machineSrc == null || !this.machineSrc.machine().isPresent()) {
            return null;
        }
        return (AdvCraftingBlockEntity) this.machineSrc.machine().get();
    }

    @Nullable
    public IGrid getGrid() {
        final IGridNode node = getNode();
        return node != null ? node.getGrid() : null;
    }

    public void cancelJobs() {
        final List<UUID> ids = new ArrayList<>(this.activeCpus.keySet());
        for (final UUID id : ids) {
            killCpu(id, false);
        }
        updateGridForChangedCpu(this);
    }

    public void cancelJob(final UUID uniqueId) {
        if (this.activeCpus.containsKey(uniqueId)) {
            killCpu(uniqueId, true);
        }
    }

    /**
     * AE2 8.4.7 submits ICraftingJob instances rather than the newer
     * ICraftingPlan/CraftingSubmitResult pair. The per-job logic will finish the
     * old-job conversion; the cluster already owns the correct allocation and
     * lifecycle semantics.
     */
    @Nullable
    public ICraftingLink submitJob(
            final IGrid grid,
            final ICraftingJob job,
            final IActionSource src,
            @Nullable final ICraftingRequester requestingMachine) {
        if (!isActive() || job == null || job.isSimulation()) {
            return null;
        }
        if (getAvailableStorage() < job.getByteTotal()) {
            return null;
        }

        final UUID uniqueId = UUID.randomUUID();
        final AdvCraftingCPU newCpu = new AdvCraftingCPU(this, uniqueId, job.getByteTotal());
        final ICraftingLink result = newCpu.craftingLogic.trySubmitJob(grid, job, src, requestingMachine);
        if (newCpu.craftingLogic.hasJob()) {
            this.activeCpus.put(uniqueId, newCpu);
            recalculateRemainingStorage();
            updateGridForChangedCpu(this);
        }
        return result;
    }

    private void killCpu(final UUID id, final boolean updateGrid) {
        final AdvCraftingCPU cpu = this.activeCpus.get(id);
        if (cpu == null) {
            return;
        }
        cpu.craftingLogic.cancel();
        cpu.craftingLogic.markForDeletion();
        recalculateRemainingStorage();
        if (updateGrid) {
            updateGridForChangedCpu(this);
        }
    }

    protected void deactivate(final UUID uniqueId) {
        if (uniqueId != null) {
            this.activeCpus.remove(uniqueId);
            recalculateRemainingStorage();
            updateGridForChangedCpu(this);
        }
    }

    public List<AdvCraftingCPU> getActiveCPUs() {
        final List<AdvCraftingCPU> list = new ArrayList<>();
        final List<UUID> killList = new ArrayList<>();
        for (final Map.Entry<UUID, AdvCraftingCPU> entry : this.activeCpus.entrySet()) {
            final AdvCraftingCPU cpu = entry.getValue();
            if (cpu.craftingLogic.hasJob() || cpu.craftingLogic.isMarkedForDeletion()) {
                list.add(cpu);
            } else {
                killList.add(entry.getKey());
            }
        }
        for (final UUID cpuId : killList) {
            this.activeCpus.remove(cpuId);
        }
        if (!killList.isEmpty()) {
            recalculateRemainingStorage();
        }
        return list;
    }

    public AdvCraftingCPU getRemainingCapacityCPU() {
        if (this.remainingStorageCpu == null
                || this.remainingStorageCpu.getAvailableStorage() != this.remainingStorage) {
            this.remainingStorageCpu = new AdvCraftingCPU(this, this.remainingStorage);
        }
        return this.remainingStorageCpu;
    }

    public long getAvailableStorage() {
        return this.remainingStorage;
    }

    public int getCoProcessors() {
        int coprocessors = this.accelerator;
        if (this.acceleratorMultiplier > 0) {
            coprocessors *= this.acceleratorMultiplier;
        }
        return coprocessors;
    }

    @Nullable
    public ITextComponent getName() {
        return this.myName;
    }

    @Nullable
    public IGridNode getNode() {
        final AdvCraftingBlockEntity core = getCore();
        return core != null ? core.getActionableNode() : null;
    }

    public boolean isActive() {
        final IGridNode node = getNode();
        return node != null && node.isActive();
    }

    public void writeToNBT(final CompoundNBT data) {
        final ListNBT cpuList = new ListNBT();
        for (final Map.Entry<UUID, AdvCraftingCPU> entry : this.activeCpus.entrySet()) {
            final AdvCraftingCPU cpu = entry.getValue();
            if (cpu == null) {
                continue;
            }

            final CompoundNBT pair = new CompoundNBT();
            pair.putString("key", entry.getKey().toString());
            pair.putLong("bytes", cpu.getAvailableStorage());
            final CompoundNBT cpuTag = new CompoundNBT();
            cpu.writeToNBT(cpuTag);
            pair.put("cpu", cpuTag);
            cpuList.add(pair);
        }
        data.put("cpuList", cpuList);
        data.putString("selectionMode", this.selectionMode.name());
    }

    void done() {
        final AdvCraftingBlockEntity core = this.getCore();
        if (core == null) {
            return;
        }

        core.setCoreBlock(true);
        if (core.getPreviousState() != null) {
            this.readFromNBT(core.getPreviousState());
            core.setPreviousState(null);
        }
        this.updateName();
    }

    public void readFromNBT(final CompoundNBT data) {
        this.activeCpus.clear();
        final ListNBT cpuList = data.getList("cpuList", 10);
        for (int i = 0; i < cpuList.size(); i++) {
            final CompoundNBT pair = cpuList.getCompound(i);
            UUID id;
            try {
                id = UUID.fromString(pair.getString("key"));
            } catch (IllegalArgumentException ignored) {
                id = UUID.randomUUID();
            }

            final long bytes = pair.getLong("bytes");
            final AdvCraftingCPU cpu = new AdvCraftingCPU(this, id, bytes);
            this.activeCpus.put(id, cpu);
            cpu.readFromNBT(pair.getCompound("cpu"));
        }

        this.selectionMode = AdvCpuSelectionMode.fromName(data.getString("selectionMode"));
        recalculateRemainingStorage();
    }

    public void updateName() {
        this.myName = null;
        for (final AdvCraftingBlockEntity tile : this.blockEntities) {
            if (tile.hasCustomInventoryName()) {
                if (this.myName == null) {
                    this.myName = tile.getCustomInventoryName().deepCopy();
                } else {
                    this.myName = this.myName.deepCopy()
                            .appendString(" ")
                            .appendSibling(tile.getCustomInventoryName());
                }
            }
        }
    }

    @Nullable
    public World getLevel() {
        final AdvCraftingBlockEntity core = this.getCore();
        return core != null ? core.getWorld() : null;
    }

    public void breakCluster() {
        final AdvCraftingBlockEntity core = this.getCore();
        if (core != null) {
            core.breakCluster();
        }
    }

    public AdvCpuSelectionMode getSelectionMode() {
        return this.selectionMode;
    }

    public void setSelectionMode(final AdvCpuSelectionMode selectionMode) {
        this.selectionMode = selectionMode == null ? AdvCpuSelectionMode.ANY : selectionMode;
        markDirty();
    }

    public boolean canBeAutoSelectedFor(final IActionSource source) {
        switch (getSelectionMode()) {
            case PLAYER_ONLY:
                return source.player().isPresent();
            case MACHINE_ONLY:
                return !source.player().isPresent();
            case ANY:
            default:
                return true;
        }
    }

    public boolean isPreferredFor(final IActionSource source) {
        switch (getSelectionMode()) {
            case PLAYER_ONLY:
                return source.player().isPresent();
            case MACHINE_ONLY:
                return !source.player().isPresent();
            case ANY:
            default:
                return false;
        }
    }
}
