package net.pedroksl.advanced_ae.common.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.config.CpuSelectionMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;
import appeng.crafting.CraftingPlan;
import appeng.crafting.execution.CraftingSubmitResult;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigManager;

public class AdvCraftingCPUCluster implements IAECluster {

    private final BlockPos boundsMin;
    private final BlockPos boundsMax;
    private final HashMap<UUID, AdvCraftingCPU> activeCpus = new HashMap<UUID, AdvCraftingCPU>();
    private AdvCraftingCPU remainingStorageCpu;
    private final List<AdvCraftingBlockEntity> blockEntities = new ArrayList<AdvCraftingBlockEntity>();
    private final List<CraftingMonitorBlockEntity> status = new ArrayList<CraftingMonitorBlockEntity>();
    private final ConfigManager configManager = new ConfigManager(this::markDirty);
    private Component myName = null;
    private boolean isDestroyed = false;
    private long storage = 0;
    private long storageMultiplier = 0;
    private long remainingStorage = 0;
    private MachineSource machineSrc = null;
    private int accelerator = 0;
    private int acceleratorMultiplier = 0;

    public AdvCraftingCPUCluster(BlockPos boundsMin, BlockPos boundsMax) {
        this.boundsMin = boundsMin.immutable();
        this.boundsMax = boundsMax.immutable();
        this.configManager.registerSetting(Settings.CPU_SELECTION_MODE, CpuSelectionMode.ANY);
    }

    @Override
    public boolean isDestroyed() { return this.isDestroyed; }
    @Override
    public BlockPos getBoundsMin() { return this.boundsMin; }
    @Override
    public BlockPos getBoundsMax() { return this.boundsMax; }

    @Override
    public void updateStatus(boolean b) {
        for (AdvCraftingBlockEntity r : this.blockEntities) r.updateSubType(true);
    }

    @Override
    public void destroy() {
        if (this.isDestroyed) return;
        this.isDestroyed = true;
        boolean ownsModification = !MBCalculator.isModificationInProgress();
        if (ownsModification) MBCalculator.setModificationInProgress(this);
        try {
            updateGridForChangedCpu(null);
        } finally {
            if (ownsModification) MBCalculator.setModificationInProgress(null);
        }
    }

    private void updateGridForChangedCpu(AdvCraftingCPUCluster cluster) {
        boolean posted = false;
        for (AdvCraftingBlockEntity r : this.blockEntities) {
            final IGridNode n = r.getActionableNode();
            if (n != null && !posted) {
                n.getGrid().postEvent(new GridCraftingCpuChange(n));
                posted = true;
            }
            r.updateStatus(cluster);
        }
    }

    @Override
    public Iterator<AdvCraftingBlockEntity> getBlockEntities() { return this.blockEntities.iterator(); }
    public int numBlockEntities() { return this.blockEntities.size(); }

    public List<ListCraftingInventory> getInventories() {
        List<ListCraftingInventory> list = new ArrayList<ListCraftingInventory>();
        for (AdvCraftingCPU cpu : this.activeCpus.values()) list.add(cpu.getInventory());
        return list;
    }

    void addBlockEntity(AdvCraftingBlockEntity te) {
        if (this.machineSrc == null || te.isCoreBlock()) this.machineSrc = new MachineSource(te);
        te.setCoreBlock(false);
        te.saveChanges();
        this.blockEntities.add(0, te);
        if (te.getStorageBytes() > 0) {
            this.storage += te.getStorageBytes();
            recalculateRemainingStorage();
        }
        if (te.getStorageMultiplier() > 0) {
            this.storageMultiplier += te.getStorageMultiplier();
            recalculateRemainingStorage();
        }
        if (te.getAcceleratorThreads() > 0) {
            if (te.getAcceleratorThreads() <= 16) this.accelerator += te.getAcceleratorThreads();
            else throw new IllegalArgumentException("Co-processor threads may not exceed 16 per single unit block.");
        }
        if (te.getAccelerationMultiplier() > 0) this.acceleratorMultiplier += te.getAccelerationMultiplier();
    }

    public void recalculateRemainingStorage() {
        long totalStorage = this.storage;
        if (this.storageMultiplier > 0) totalStorage *= this.storageMultiplier;
        long usedStorage = 0;
        for (AdvCraftingCPU cpu : this.activeCpus.values()) usedStorage += cpu.getAvailableStorage();
        this.remainingStorage = totalStorage - usedStorage;
    }

    public void markDirty() { this.getCore().saveChanges(); }

    public void updateOutput(GenericStack finalOutput) {
        GenericStack send = finalOutput;
        if (finalOutput != null && finalOutput.amount() <= 0) send = null;
        for (CraftingMonitorBlockEntity t : this.status) t.setJob(send);
    }

    public IActionSource getSrc() { return Objects.requireNonNull(this.machineSrc); }

    private AdvCraftingBlockEntity getCore() {
        if (this.machineSrc == null) return null;
        return (AdvCraftingBlockEntity) this.machineSrc.machine().get();
    }

    @Nullable
    public IGrid getGrid() {
        IGridNode node = getNode();
        return node != null ? node.getGrid() : null;
    }

    public void cancelJobs() {
        for (UUID id : activeCpus.keySet()) killCpu(id, false);
    }

    public void cancelJob(UUID uniqueId) {
        AdvCraftingCPU cpu = activeCpus.get(uniqueId);
        if (cpu != null) killCpu(uniqueId);
    }

    public ICraftingSubmitResult submitJob(
            IGrid grid, ICraftingPlan plan, IActionSource src, ICraftingRequester requestingMachine) {
        if (!isActive()) return CraftingSubmitResult.CPU_OFFLINE;
        if (getAvailableStorage() < plan.bytes()) return CraftingSubmitResult.CPU_TOO_SMALL;

        UUID uniqueId = UUID.randomUUID();
        AdvCraftingCPU newCpu = new AdvCraftingCPU(this, uniqueId, plan.bytes());
        ICraftingSubmitResult submitResult = newCpu.craftingLogic.trySubmitJob(grid, plan, src, requestingMachine);
        if (submitResult.successful()) {
            this.activeCpus.put(uniqueId, newCpu);
            recalculateRemainingStorage();
            updateGridForChangedCpu(this);
        }
        return submitResult;
    }

    private void killCpu(UUID id, boolean updateGrid) {
        AdvCraftingCPU cpu = this.activeCpus.get(id);
        cpu.craftingLogic.cancel();
        cpu.craftingLogic.markForDeletion();
        recalculateRemainingStorage();
        if (updateGrid) updateGridForChangedCpu(this);
    }

    private void killCpu(UUID uniqueId) { killCpu(uniqueId, true); }

    protected void deactivate(UUID uniqueId) {
        this.activeCpus.remove(uniqueId);
        recalculateRemainingStorage();
        updateGridForChangedCpu(this);
    }

    public List<AdvCraftingCPU> getActiveCPUs() {
        ArrayList<AdvCraftingCPU> list = new ArrayList<AdvCraftingCPU>();
        ArrayList<UUID> killList = new ArrayList<UUID>();
        for (Map.Entry<UUID, AdvCraftingCPU> cpuEntry : activeCpus.entrySet()) {
            AdvCraftingCPU cpu = cpuEntry.getValue();
            if (cpu.craftingLogic.hasJob() || cpu.craftingLogic.isMarkedForDeletion()) list.add(cpu);
            else killList.add(cpuEntry.getKey());
        }
        for (UUID cpuId : killList) killCpu(cpuId);
        return list;
    }

    public AdvCraftingCPU getRemainingCapacityCPU() {
        if (this.remainingStorageCpu == null || this.remainingStorageCpu.getAvailableStorage() != this.remainingStorage) {
            this.remainingStorageCpu = new AdvCraftingCPU(this, this.remainingStorage);
        }
        return this.remainingStorageCpu;
    }

    @Nullable
    public CraftingJobStatus getJobStatus(UUID uniqueId) {
        AdvCraftingCPU cpu = activeCpus.get(uniqueId);
        return cpu != null ? cpu.getJobStatus() : null;
    }

    public long getAvailableStorage() { return this.remainingStorage; }

    public int getCoProcessors() {
        int coprocessors = this.accelerator;
        if (this.acceleratorMultiplier > 0) coprocessors *= this.acceleratorMultiplier;
        return coprocessors;
    }

    public Component getName() { return this.myName; }

    @Nullable
    public IGridNode getNode() {
        AdvCraftingBlockEntity core = getCore();
        return core != null ? core.getActionableNode() : null;
    }

    public boolean isActive() {
        IGridNode node = getNode();
        return node != null && node.isActive();
    }

    public void writeToNBT(CompoundTag data) {
        ListTag listCpus = new ListTag();
        for (Map.Entry<UUID, AdvCraftingCPU> cpu : activeCpus.entrySet()) {
            StringTag keyTag = StringTag.valueOf(cpu.getKey().toString());
            LongTag bytesTag = LongTag.valueOf(cpu.getValue().getAvailableStorage());
            CompoundTag cpuTag = new CompoundTag();
            cpu.getValue().writeToNBT(cpuTag);
            CompoundTag pair = new CompoundTag();
            pair.put("key", keyTag);
            pair.put("bytes", bytesTag);
            pair.put("cpu", cpuTag);
            listCpus.add(pair);
        }
        data.put("cpuList", listCpus);
        this.configManager.writeToNBT(data);
    }

    void done() {
        final AdvCraftingBlockEntity core = this.getCore();
        core.setCoreBlock(true);
        if (core.getPreviousState() != null) {
            this.readFromNBT(core.getPreviousState());
            core.setPreviousState(null);
        }
        this.updateName();
    }

    public void readFromNBT(CompoundTag data) {
        ListTag cpuList = (ListTag) data.get("cpuList");
        if (cpuList != null) {
            for (int x = 0; x < cpuList.size(); x++) {
                CompoundTag pair = cpuList.getCompound(x);
                UUID id;
                long bytes;
                Tag keyTag = pair.get("key");
                if (keyTag instanceof CompoundTag) {
                    CraftingPlan plan = readCraftingPlanFromNBT((CompoundTag) keyTag);
                    id = UUID.randomUUID();
                    bytes = plan.bytes();
                } else {
                    try {
                        id = UUID.fromString(pair.getString("key"));
                    } catch (IllegalArgumentException e) {
                        id = UUID.randomUUID();
                    }
                    bytes = pair.getLong("bytes");
                }

                AdvCraftingCPU cpu = new AdvCraftingCPU(this, id, bytes);
                this.activeCpus.put(id, cpu);
                cpu.readFromNBT(pair.getCompound("cpu"));
            }
        }
        this.configManager.readFromNBT(data);
        recalculateRemainingStorage();
    }

    private CraftingPlan readCraftingPlanFromNBT(CompoundTag tag) {
        GenericStack output = GenericStack.readTag(tag.getCompound("output"));
        long bytes = tag.getLong("bytes");
        boolean simulation = tag.getBoolean("simulation");
        boolean multiplePaths = tag.getBoolean("multiplePaths");
        return new CraftingPlan(output, bytes, simulation, multiplePaths, null, null, null, null);
    }

    public void updateName() {
        this.myName = null;
        for (AdvCraftingBlockEntity te : this.blockEntities) {
            if (te.hasCustomName()) {
                if (this.myName != null) this.myName.copy().append(" ").append(te.getCustomName());
                else this.myName = te.getCustomName().copy();
            }
        }
    }

    public Level getLevel() { return this.getCore().getLevel(); }

    public void breakCluster() {
        final AdvCraftingBlockEntity t = this.getCore();
        if (t != null) t.breakCluster();
    }

    public CpuSelectionMode getSelectionMode() { return this.configManager.getSetting(Settings.CPU_SELECTION_MODE); }
    public IConfigManager getConfigManager() { return configManager; }

    public boolean canBeAutoSelectedFor(IActionSource source) {
        switch (getSelectionMode()) {
            case PLAYER_ONLY:
                return source.player().isPresent();
            case MACHINE_ONLY:
                return source.player().isEmpty();
            case ANY:
            default:
                return true;
        }
    }

    public boolean isPreferredFor(IActionSource source) {
        switch (getSelectionMode()) {
            case PLAYER_ONLY:
                return source.player().isPresent();
            case MACHINE_ONLY:
                return source.player().isEmpty();
            case ANY:
            default:
                return false;
        }
    }
}
