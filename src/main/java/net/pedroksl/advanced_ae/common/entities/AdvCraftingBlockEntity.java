package net.pedroksl.advanced_ae.common.entities;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCalculator;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCluster;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.me.cluster.IAEMultiBlock;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.AENetworkProxyMultiblock;
import appeng.tile.crafting.CraftingCubeModelData;
import appeng.tile.grid.AENetworkTileEntity;
import net.minecraftforge.client.model.data.IModelData;

public class AdvCraftingBlockEntity extends AENetworkTileEntity
        implements IAEMultiBlock<AdvCraftingCPUCluster>, IPowerChannelState {

    private final AdvCraftingCPUCalculator calc = new AdvCraftingCPUCalculator(this);
    private CompoundNBT previousState;
    private boolean isCoreBlock;
    private AdvCraftingCPUCluster cluster;

    public AdvCraftingBlockEntity(TileEntityType<?> tileEntityType, BlockPos ignoredPos, BlockState ignoredState) {
        super(tileEntityType);
        this.getProxy().setFlags(GridFlags.MULTIBLOCK, GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setValidSides(EnumSet.noneOf(Direction.class));
    }

    @Override
    protected AENetworkProxy createProxy() {
        return new AENetworkProxyMultiblock(this, "proxy", this.getItemFromTile(this), true);
    }

    @Override
    protected ItemStack getItemFromTile(final Object obj) {
        return new ItemStack(getUnitBlock().type.getItemFromType());
    }

    @Override
    public void setName(final String name) {
        super.setName(name);
        if (this.cluster != null) {
            this.cluster.updateName();
        }
    }

    public AAEAbstractCraftingUnitBlock<?> getUnitBlock() {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return AAEBlocks.QUANTUM_UNIT.block();
        }
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (state.getBlock() instanceof AAEAbstractCraftingUnitBlock) {
            return (AAEAbstractCraftingUnitBlock<?>) state.getBlock();
        }
        return AAEBlocks.QUANTUM_UNIT.block();
    }

    public long getStorageBytes() {
        return getUnitBlock().type.getStorageBytes();
    }

    public int getStorageMultiplier() {
        return getUnitBlock().type.getStorageMultiplier();
    }

    public int getAcceleratorThreads() {
        return getUnitBlock().type.getAcceleratorThreads();
    }

    public int getAccelerationMultiplier() {
        return getUnitBlock().type.getAccelerationMultiplier();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.getProxy().setVisualRepresentation(this.getItemFromTile(this));
        if (this.level != null) {
            this.calc.calculateMultiblock(this.level, this.worldPosition);
        }
    }

    public void updateMultiBlock(final BlockPos changedPos) {
        if (this.level != null) {
            this.calc.updateMultiblockAfterNeighborUpdate(this.level, this.worldPosition, changedPos);
        }
    }

    public void updateStatus(final AdvCraftingCPUCluster newCluster) {
        if (this.cluster != null && this.cluster != newCluster) {
            this.cluster.breakCluster();
        }
        this.cluster = newCluster;
        this.updateSubType(true);
    }

    public void updateSubType(final boolean updateFormed) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final boolean formed = this.isFormed();
        final boolean power = this.getProxy().isReady() && this.getProxy().isActive();
        final BlockState current = this.level.getBlockState(this.worldPosition);

        if (current.getBlock() instanceof AAEAbstractCraftingUnitBlock) {
            final AAECraftingUnitType type = this.getUnitBlock().type;
            int lightLevel = type == AAECraftingUnitType.QUANTUM_CORE ? 12 : 0;
            lightLevel = formed && power ? lightLevel : 0;
            final boolean multiblocked = this.cluster != null && this.cluster.numBlockEntities() > 1;

            final BlockState newState = current.setValue(AAEAbstractCraftingUnitBlock.POWERED, power)
                    .setValue(AAEAbstractCraftingUnitBlock.FORMED, formed)
                    .setValue(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL, lightLevel)
                    .setValue(AAEAbstractCraftingUnitBlock.MULTIBLOCKED, multiblocked);

            if (current != newState) {
                this.level.setBlock(this.worldPosition, newState, 2);
            }
        }

        if (updateFormed) {
            if (!formed) {
                this.getProxy().setValidSides(EnumSet.noneOf(Direction.class));
            } else if (getUnitBlock().type == AAECraftingUnitType.QUANTUM_CORE) {
                this.getProxy().setValidSides(EnumSet.of(Direction.UP, Direction.DOWN));
            } else {
                this.getProxy().setValidSides(EnumSet.allOf(Direction.class));
            }
        }
    }

    public boolean isFormed() {
        if (isRemote()) {
            return this.level != null
                    && this.level.getBlockState(this.worldPosition).getValue(AAEAbstractCraftingUnitBlock.FORMED);
        }
        return this.cluster != null;
    }

    @Override
    public CompoundNBT save(final CompoundNBT data) {
        super.save(data);
        data.putBoolean("core", this.isCoreBlock());
        if (this.isCoreBlock() && this.cluster != null) {
            this.cluster.writeToNBT(data);
        }
        return data;
    }

    @Override
    public void load(final BlockState blockState, final CompoundNBT data) {
        super.load(blockState, data);
        this.setCoreBlock(data.getBoolean("core"));
        if (this.isCoreBlock()) {
            if (this.cluster != null) {
                this.cluster.readFromNBT(data);
            } else {
                this.setPreviousState(data.copy());
            }
        }
    }

    @Override
    public void disconnect(final boolean update) {
        if (this.cluster != null) {
            this.cluster.destroy();
            if (update) {
                this.updateSubType(true);
            }
        }
    }

    @Override
    public AdvCraftingCPUCluster getCluster() {
        return this.cluster;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @MENetworkEventSubscribe
    public void onPowerStateChange(final MENetworkChannelsChanged event) {
        this.updateSubType(false);
    }

    @MENetworkEventSubscribe
    public void onPowerStateChange(final MENetworkPowerStatusChange event) {
        this.updateSubType(false);
    }

    public void breakCluster() {
        requestModelDataUpdate();
        if (this.cluster != null) {
            this.cluster.cancelJobs();
            this.cluster.destroy();
        }
    }

    @Override
    public boolean isPowered() {
        if (isRemote()) {
            return this.level != null
                    && this.level.getBlockState(this.worldPosition).getValue(AAEAbstractCraftingUnitBlock.POWERED);
        }
        return this.getProxy().isActive();
    }

    public boolean isMultiblocked() {
        if (isRemote()) {
            return this.level != null
                    && this.level.getBlockState(this.worldPosition).getValue(AAEAbstractCraftingUnitBlock.MULTIBLOCKED);
        }
        return this.cluster != null && this.cluster.numBlockEntities() > 1;
    }

    @Override
    public boolean isActive() {
        if (!isRemote()) {
            return this.getProxy().isActive();
        }
        return this.isPowered() && this.isFormed();
    }

    public boolean isCoreBlock() {
        return this.isCoreBlock;
    }

    public void setCoreBlock(final boolean coreBlock) {
        this.isCoreBlock = coreBlock;
    }

    public CompoundNBT getPreviousState() {
        return this.previousState;
    }

    public void setPreviousState(final CompoundNBT previousState) {
        this.previousState = previousState;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new CraftingCubeModelData(getUp(), getForward(), getConnections());
    }

    protected EnumSet<Direction> getConnections() {
        if (this.level == null) {
            return EnumSet.noneOf(Direction.class);
        }
        final EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);
        for (final Direction facing : Direction.values()) {
            if (this.isConnected(this.level, this.worldPosition, facing)) {
                connections.add(facing);
            }
        }
        return connections;
    }

    private boolean isConnected(final IBlockReader level, final BlockPos pos, final Direction side) {
        final BlockPos adjacentPos = pos.relative(side);
        return level.getBlockState(adjacentPos).getBlock() instanceof AAEAbstractCraftingUnitBlock;
    }
}
