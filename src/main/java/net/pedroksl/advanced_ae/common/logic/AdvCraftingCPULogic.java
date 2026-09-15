package net.pedroksl.advanced_ae.common.logic;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;

/**
 * Minimal AE2 8 compatible crafting executor used by the 1.16.5 compile baseline.
 * The multiblock/storage lifecycle remains available while the newer AEKey-based
 * execution engine is backported to the older IAEItemStack crafting API.
 */
public class AdvCraftingCPULogic {
    private final AdvCraftingCPU cpu;
    private boolean markedForDeletion;

    public AdvCraftingCPULogic(AdvCraftingCPU cpu) {
        this.cpu = cpu;
    }

    @Nullable
    public ICraftingLink trySubmitJob(
            IGrid grid, ICraftingJob job, IActionSource src, @Nullable ICraftingRequester requester) {
        // Job execution is intentionally deferred until the AE2 8 crafting adapter is restored.
        return null;
    }

    public boolean hasJob() {
        return false;
    }

    public void cancel() {
        // No active job is retained by the baseline executor.
    }

    public void markForDeletion() {
        this.markedForDeletion = true;
    }

    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }

    public void writeToNBT(CompoundNBT data) {
        data.putBoolean("aaeBaselineMarkedForDeletion", this.markedForDeletion);
    }

    public void readFromNBT(CompoundNBT data) {
        this.markedForDeletion = data.getBoolean("aaeBaselineMarkedForDeletion");
    }

    public AdvCraftingCPU getCpu() {
        return this.cpu;
    }
}
