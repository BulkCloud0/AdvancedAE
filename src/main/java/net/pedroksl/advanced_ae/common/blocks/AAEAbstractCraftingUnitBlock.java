package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.AEBaseTileBlock;
import appeng.container.ContainerLocator;
import appeng.container.ContainerOpener;
import appeng.tile.AEBaseTileEntity;

public abstract class AAEAbstractCraftingUnitBlock<T extends AEBaseTileEntity> extends AEBaseTileBlock<T> {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty MULTIBLOCKED = BooleanProperty.create("multiblocked");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    public final AAECraftingUnitType type;

    public AAEAbstractCraftingUnitBlock(AbstractBlock.Properties props, AAECraftingUnitType type) {
        super(props);
        this.type = type;
        this.setDefaultState(getDefaultState()
                .with(FORMED, false)
                .with(POWERED, false)
                .with(MULTIBLOCKED, false)
                .with(LIGHT_LEVEL, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED, FORMED, MULTIBLOCKED, LIGHT_LEVEL);
    }

    @Override
    public BlockState updatePostPlacement(
            BlockState stateIn,
            Direction facing,
            BlockState facingState,
            IWorld worldIn,
            BlockPos currentPos,
            BlockPos facingPos) {
        TileEntity te = worldIn.getTileEntity(currentPos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(
            BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        AdvCraftingBlockEntity crafting = (AdvCraftingBlockEntity) this.getTileEntity(world, pos);
        if (crafting != null) {
            crafting.updateMultiBlock(fromPos);
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() == state.getBlock()) {
            return;
        }

        AdvCraftingBlockEntity crafting = (AdvCraftingBlockEntity) this.getTileEntity(world, pos);
        if (crafting != null) {
            crafting.breakCluster();
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(
            BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        AdvCraftingBlockEntity crafting = (AdvCraftingBlockEntity) this.getTileEntity(world, pos);
        if (crafting != null && crafting.isFormed() && crafting.isActive()) {
            if (!world.isRemote()) {
                ContainerOpener.openContainer(
                        AAEMenus.QUANTUM_COMPUTER.get(),
                        player,
                        ContainerLocator.forTileEntitySide(crafting, hit.getFace()));
            }
            return ActionResultType.func_233537_a_(world.isRemote());
        }

        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
