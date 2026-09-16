package net.pedroksl.advanced_ae.gui.quantumcomputer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.config.CpuSelectionMode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.stacks.GenericStack;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.me.crafting.CraftingCPUMenu;

public class QuantumComputerMenu extends CraftingCPUMenu {

    private static final CraftingCpuList EMPTY_CPU_LIST = new CraftingCpuList(Collections.<CraftingCpuListEntry>emptyList());

    private static final Comparator<CraftingCpuListEntry> CPU_COMPARATOR = Comparator.comparing(
                    (CraftingCpuListEntry e) -> e.name() == null)
            .thenComparing(e -> e.name() != null ? e.name().getString() : "")
            .thenComparingInt(CraftingCpuListEntry::serial);

    private static final String ACTION_SELECT_CPU = "selectCpu";

    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap = new WeakHashMap<ICraftingCPU, Integer>();
    private int nextCpuSerial = 1;
    private List<AdvCraftingCPU> lastCpuSet = Collections.emptyList();
    private int lastUpdate = 0;

    @GuiSync(8)
    public CraftingCpuList cpuList = EMPTY_CPU_LIST;

    @Nullable
    private ICraftingCPU selectedCpu = null;

    @GuiSync(9)
    private int selectedCpuSerial = -1;

    @GuiSync(10)
    public CpuSelectionMode selectionMode = CpuSelectionMode.ANY;

    private final AdvCraftingBlockEntity host;

    public QuantumComputerMenu(int id, Inventory ip, AdvCraftingBlockEntity te) {
        super(AAEMenus.QUANTUM_COMPUTER.get(), id, ip, te);
        this.host = te;

        if (te != null && te.getCluster() != null) {
            selectionMode = te.getCluster().getSelectionMode();
        }

        this.registerClientAction(ACTION_SELECT_CPU, Integer.class, this::selectCpu);
    }

    @Override
    protected void setCPU(ICraftingCPU c) {
        super.setCPU(c);
        this.selectedCpuSerial = getOrAssignCpuSerial(c);
    }

    @Override
    public void broadcastChanges() {
        if (this.host == null) {
            super.broadcastChanges();
            return;
        }

        if (isServerSide() && this.host.getCluster() != null) {
            List<AdvCraftingCPU> newCpuSet = this.host.getCluster().getActiveCPUs();
            newCpuSet.add(this.host.getCluster().getRemainingCapacityCPU());
            if (!lastCpuSet.equals(newCpuSet) || ++lastUpdate >= 20) {
                lastCpuSet = newCpuSet;
                cpuList = createCpuList();
            }
        } else {
            lastUpdate = 20;
            if (!lastCpuSet.isEmpty()) {
                cpuList = EMPTY_CPU_LIST;
                lastCpuSet = Collections.emptyList();
            }
        }

        if (selectedCpuSerial != -1) {
            if (cpuList.cpus().stream().noneMatch(c -> c.serial() == selectedCpuSerial)) {
                selectCpu(-1);
            }
        }

        if (selectedCpuSerial == -1) {
            for (CraftingCpuListEntry cpu : cpuList.cpus()) {
                if (cpu.currentJob() != null) {
                    selectCpu(cpu.serial());
                    break;
                }
            }
            if (selectedCpuSerial == -1 && !cpuList.cpus().isEmpty()) {
                selectCpu(cpuList.cpus().get(0).serial());
            }
        }

        if (this.host.getCluster() != null) {
            selectionMode = this.host.getCluster().getSelectionMode();
        }

        super.broadcastChanges();
    }

    private CraftingCpuList createCpuList() {
        ArrayList<CraftingCpuListEntry> entries = new ArrayList<CraftingCpuListEntry>(lastCpuSet.size());
        for (AdvCraftingCPU cpu : lastCpuSet) {
            int serial = getOrAssignCpuSerial(cpu);
            AdvCraftingCPU.JobStatus status = cpu.getJobStatus();
            float progress = 0f;
            if (status != null && status.totalItems() > 0) {
                progress = (float) (status.progress() / (double) status.totalItems());
            }
            entries.add(new CraftingCpuListEntry(
                    serial,
                    cpu.getAvailableStorage(),
                    cpu.getCoProcessors(),
                    cpu.getName(),
                    cpu.getSelectionMode(),
                    status != null ? status.crafting() : null,
                    progress,
                    status != null ? status.elapsedTimeNanos() : 0));
        }
        Collections.sort(entries, CPU_COMPARATOR);
        return new CraftingCpuList(entries);
    }

    private int getOrAssignCpuSerial(ICraftingCPU cpu) {
        if (this.cpuSerialMap == null) {
            this.cpuSerialMap = new WeakHashMap<ICraftingCPU, Integer>();
        }
        Integer serial = cpuSerialMap.get(cpu);
        if (serial == null) {
            serial = Integer.valueOf(nextCpuSerial++);
            cpuSerialMap.put(cpu, serial);
        }
        return serial.intValue();
    }

    @Override
    public boolean allowConfiguration() {
        return false;
    }

    public void selectCpu(int serial) {
        if (isClientSide()) {
            selectedCpuSerial = serial;
            sendClientAction(ACTION_SELECT_CPU, serial);
        } else {
            ICraftingCPU newSelectedCpu = null;
            if (serial != -1) {
                for (AdvCraftingCPU cpu : lastCpuSet) {
                    Integer cpuSerial = cpuSerialMap.get(cpu);
                    if (cpuSerial != null && cpuSerial.intValue() == serial) {
                        newSelectedCpu = cpu;
                        break;
                    }
                }
            }

            if (newSelectedCpu != selectedCpu) {
                setCPU(newSelectedCpu);
            }
        }
    }

    public int getSelectedCpuSerial() {
        return selectedCpuSerial;
    }

    public CpuSelectionMode getSelectionMode() {
        return this.selectionMode;
    }

    public static final class CraftingCpuList implements PacketWritable {
        private final List<CraftingCpuListEntry> cpus;

        public CraftingCpuList(List<CraftingCpuListEntry> cpus) {
            this.cpus = cpus;
        }

        public CraftingCpuList(FriendlyByteBuf data) {
            this(readFromPacket(data));
        }

        public List<CraftingCpuListEntry> cpus() {
            return cpus;
        }

        private static List<CraftingCpuListEntry> readFromPacket(FriendlyByteBuf data) {
            int count = data.readInt();
            ArrayList<CraftingCpuListEntry> result = new ArrayList<CraftingCpuListEntry>(count);
            for (int i = 0; i < count; i++) {
                result.add(CraftingCpuListEntry.readFromPacket(data));
            }
            return result;
        }

        @Override
        public void writeToPacket(FriendlyByteBuf data) {
            data.writeInt(cpus.size());
            for (CraftingCpuListEntry entry : cpus) {
                entry.writeToPacket(data);
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CraftingCpuList && Objects.equals(cpus, ((CraftingCpuList) obj).cpus);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cpus);
        }
    }

    public static final class CraftingCpuListEntry {
        private final int serial;
        private final long storage;
        private final int coProcessors;
        private final Component name;
        private final CpuSelectionMode mode;
        private final GenericStack currentJob;
        private final float progress;
        private final long elapsedTimeNanos;

        public CraftingCpuListEntry(int serial, long storage, int coProcessors, Component name,
                CpuSelectionMode mode, GenericStack currentJob, float progress, long elapsedTimeNanos) {
            this.serial = serial;
            this.storage = storage;
            this.coProcessors = coProcessors;
            this.name = name;
            this.mode = mode;
            this.currentJob = currentJob;
            this.progress = progress;
            this.elapsedTimeNanos = elapsedTimeNanos;
        }

        public int serial() { return serial; }
        public long storage() { return storage; }
        public int coProcessors() { return coProcessors; }
        public Component name() { return name; }
        public CpuSelectionMode mode() { return mode; }
        public GenericStack currentJob() { return currentJob; }
        public float progress() { return progress; }
        public long elapsedTimeNanos() { return elapsedTimeNanos; }

        public static CraftingCpuListEntry readFromPacket(FriendlyByteBuf data) {
            return new CraftingCpuListEntry(
                    data.readInt(),
                    data.readLong(),
                    data.readInt(),
                    data.readBoolean() ? data.readComponent() : null,
                    data.readEnum(CpuSelectionMode.class),
                    GenericStack.readBuffer(data),
                    data.readFloat(),
                    data.readVarLong());
        }

        public void writeToPacket(FriendlyByteBuf data) {
            data.writeInt(serial);
            data.writeLong(storage);
            data.writeInt(coProcessors);
            data.writeBoolean(name != null);
            if (name != null) {
                data.writeComponent(name);
            }
            data.writeEnum(mode);
            GenericStack.writeBuffer(currentJob, data);
            data.writeFloat(progress);
            data.writeVarLong(elapsedTimeNanos);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CraftingCpuListEntry)) return false;
            CraftingCpuListEntry other = (CraftingCpuListEntry) obj;
            return serial == other.serial && storage == other.storage && coProcessors == other.coProcessors
                    && Float.compare(progress, other.progress) == 0 && elapsedTimeNanos == other.elapsedTimeNanos
                    && Objects.equals(name, other.name) && mode == other.mode && Objects.equals(currentJob, other.currentJob);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serial, storage, coProcessors, name, mode, currentJob, progress, elapsedTimeNanos);
        }
    }
}
