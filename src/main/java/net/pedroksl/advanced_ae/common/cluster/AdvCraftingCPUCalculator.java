package net.pedroksl.advanced_ae.common.cluster;

import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.util.AEPartLocation;
import appeng.me.cluster.IAEMultiBlock;
import appeng.me.cluster.MBCalculator;

public class AdvCraftingCPUCalculator extends MBCalculator<AdvCraftingBlockEntity, AdvCraftingCPUCluster> {

    public AdvCraftingCPUCalculator(final AdvCraftingBlockEntity tile) {
        super(tile);
    }

    @Override
    public boolean checkMultiblockScale(final BlockPos min, final BlockPos max) {
        final int maxSize = AAEConfig.instance().getQuantumComputerMaxSize() - 1;

        if (max.getX() - min.getX() > maxSize) {
            return false;
        }

        if (max.getY() - min.getY() > maxSize) {
            return false;
        }

        return max.getZ() - min.getZ() <= maxSize;
    }

    @Override
    public AdvCraftingCPUCluster createCluster(final World level, final BlockPos min, final BlockPos max) {
        return new AdvCraftingCPUCluster(min, max);
    }

    @Override
    public boolean verifyInternalStructure(final World level, final BlockPos min, final BlockPos max) {
        boolean core = false;
        boolean storage = false;
        int entangler = 0;
        final int entanglerLimit = AAEConfig.instance().getQuantumComputermaxDataEntanglers();
        int multi = 0;
        final int multiLimit = AAEConfig.instance().getQuantumComputerMaxMultiThreaders();

        for (BlockPos blockPos : BlockPos.getAllInBoxMutable(min, max)) {
            final IAEMultiBlock<?> multiblock = (IAEMultiBlock<?>) level.getTileEntity(blockPos);

            if (multiblock == null || !multiblock.isValid()) {
                return false;
            }

            if (!(multiblock instanceof AdvCraftingBlockEntity)) {
                return false;
            }

            final AdvCraftingBlockEntity advEntity = (AdvCraftingBlockEntity) multiblock;
            final boolean isBoundary = blockPos.getX() == min.getX()
                    || blockPos.getY() == min.getY()
                    || blockPos.getZ() == min.getZ()
                    || blockPos.getX() == max.getX()
                    || blockPos.getY() == max.getY()
                    || blockPos.getZ() == max.getZ();

            switch ((AAECraftingUnitType) advEntity.getUnitBlock().type) {
                case QUANTUM_CORE:
                    if (min.equals(max)) {
                        return true;
                    }

                    if (!isBoundary && !core) {
                        core = true;
                    } else {
                        return false;
                    }
                    break;
                case STRUCTURE:
                    if (!isBoundary) {
                        return false;
                    }
                    break;
                case STORAGE_MULTIPLIER:
                    if (!isBoundary && entangler < entanglerLimit) {
                        entangler++;
                    } else {
                        return false;
                    }
                    break;
                case MULTI_THREADER:
                    if (!isBoundary && multi < multiLimit) {
                        multi++;
                    } else {
                        return false;
                    }
                    break;
                default:
                    if (isBoundary) {
                        return false;
                    }
                    break;
            }

            if (!storage) {
                storage = advEntity.getStorageBytes() > 0;
            }
        }

        return storage && core;
    }

    @Override
    public void updateTiles(
            final AdvCraftingCPUCluster cluster, final World level, final BlockPos min, final BlockPos max) {
        for (BlockPos blockPos : BlockPos.getAllInBoxMutable(min, max)) {
            final AdvCraftingBlockEntity tile = (AdvCraftingBlockEntity) level.getTileEntity(blockPos);
            tile.updateStatus(cluster);
            cluster.addBlockEntity(tile);
        }

        cluster.done();

        final Iterator<AdvCraftingBlockEntity> iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            final IGridHost gridHost = iterator.next();
            final IGridNode node = gridHost.getGridNode(AEPartLocation.INTERNAL);
            if (node != null) {
                final IGrid grid = node.getGrid();
                if (grid != null) {
                    grid.postEvent(new MENetworkCraftingCpuChange(node));
                    return;
                }
            }
        }
    }

    @Override
    public boolean isValidTile(final TileEntity tile) {
        return tile instanceof AdvCraftingBlockEntity;
    }
}
